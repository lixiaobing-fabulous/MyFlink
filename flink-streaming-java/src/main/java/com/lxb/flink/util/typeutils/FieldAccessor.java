package com.lxb.flink.util.typeutils;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeinfo.BasicTypeInfo;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.common.typeuitils.CompositeType;
import com.lxb.flink.api.java.tupple.Tuple;
import com.lxb.flink.api.java.typeutils.TupleTypeInfo;
import com.lxb.flink.api.java.typeutils.TupleTypeInfoBase;
import com.lxb.flink.api.java.typeutils.runtime.FieldSerializer;
import com.lxb.flink.api.java.typeutils.runtime.TupleSerializerBase;
import scala.Product;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

public abstract class FieldAccessor<T, F> implements Serializable {
    protected TypeInformation fieldType;

    public TypeInformation<F> getFieldType() {
        return fieldType;
    }

    public abstract F get(T record);

    public abstract T set(T record, F fieldValue);

    static final class SimpleFieldAccessor<T> extends FieldAccessor<T, T> {

        private static final long serialVersionUID = 1L;

        public SimpleFieldAccessor(TypeInformation<T> typeInfo) {
            checkNotNull(typeInfo, "typeInfo must not be null.");

            this.fieldType = typeInfo;
        }

        @Override
        public T get(T record) {
            return record;
        }

        @Override
        public T set(T record, T fieldValue) {
            return fieldValue;
        }
    }

    static final class ArrayFieldAccessor<T, F> extends FieldAccessor<T, F> {

        private static final long serialVersionUID = 1L;

        private final int pos;

        public ArrayFieldAccessor(int pos, TypeInformation typeInfo) {
            if (pos < 0) {
                throw new CompositeType.InvalidFieldReferenceException("The " + ((Integer) pos).toString() + ". field selected on" +
                        " an array, which is an invalid index.");
            }
            checkNotNull(typeInfo, "typeInfo must not be null.");

            this.pos = pos;
            this.fieldType = BasicTypeInfo.getInfoFor(typeInfo.getTypeClass().getComponentType());
        }

        @SuppressWarnings("unchecked")
        @Override
        public F get(T record) {
            return (F) Array.get(record, pos);
        }

        @Override
        public T set(T record, F fieldValue) {
            Array.set(record, pos, fieldValue);
            return record;
        }
    }
    static final class SimpleTupleFieldAccessor<T extends Tuple, F> extends FieldAccessor<T, F> {

        private static final long serialVersionUID = 1L;

        private final int pos;

        SimpleTupleFieldAccessor(int pos, TypeInformation<T> typeInfo) {
            checkNotNull(typeInfo, "typeInfo must not be null.");
            int arity = ((TupleTypeInfo) typeInfo).getArity();
            if (pos < 0 || pos >= arity) {
                throw new CompositeType.InvalidFieldReferenceException(
                        "Tried to select " + ((Integer) pos).toString() + ". field on \"" +
                                typeInfo.toString() + "\", which is an invalid index.");
            }

            this.pos = pos;
            this.fieldType = ((TupleTypeInfo) typeInfo).getTypeAt(pos);
        }

        @SuppressWarnings("unchecked")
        @Override
        public F get(T record) {
            return (F) record.getField(pos);
        }

        @Override
        public T set(T record, F fieldValue) {
            record.setField(fieldValue, pos);
            return record;
        }
    }

    static final class RecursiveTupleFieldAccessor<T extends Tuple, R, F> extends FieldAccessor<T, F> {

        private static final long serialVersionUID = 1L;

        private final int pos;
        private final FieldAccessor<R, F> innerAccessor;

        RecursiveTupleFieldAccessor(int pos, FieldAccessor<R, F> innerAccessor, TypeInformation<T> typeInfo) {
            checkNotNull(typeInfo, "typeInfo must not be null.");
            checkNotNull(innerAccessor, "innerAccessor must not be null.");

            int arity = ((TupleTypeInfo) typeInfo).getArity();
            if (pos < 0 || pos >= arity) {
                throw new CompositeType.InvalidFieldReferenceException(
                        "Tried to select " + ((Integer) pos).toString() + ". field on \"" +
                                typeInfo.toString() + "\", which is an invalid index.");
            }

            this.pos = pos;
            this.innerAccessor = innerAccessor;
            this.fieldType = innerAccessor.fieldType;
        }

        @Override
        public F get(T record) {
            final R inner = record.getField(pos);
            return innerAccessor.get(inner);
        }

        @Override
        public T set(T record, F fieldValue) {
            final R inner = record.getField(pos);
            record.setField(innerAccessor.set(inner, fieldValue), pos);
            return record;
        }
    }

    static final class PojoFieldAccessor<T, R, F> extends FieldAccessor<T, F> {

        private static final long serialVersionUID = 1L;

        private transient Field               field;
        private final     FieldAccessor<R, F> innerAccessor;

        PojoFieldAccessor(Field field, FieldAccessor<R, F> innerAccessor) {
            checkNotNull(field, "field must not be null.");
            checkNotNull(innerAccessor, "innerAccessor must not be null.");

            this.field = field;
            this.innerAccessor = innerAccessor;
            this.fieldType = innerAccessor.fieldType;
        }

        @Override
        public F get(T pojo) {
            try {
                @SuppressWarnings("unchecked")
                final R inner = (R) field.get(pojo);
                return innerAccessor.get(inner);
            } catch (IllegalAccessException iaex) {
                // The Field class is transient and when deserializing its value we also make it accessible
                throw new RuntimeException("This should not happen since we call setAccesssible(true) in readObject."
                        + " fields: " + field + " obj: " + pojo);
            }
        }

        @Override
        public T set(T pojo, F valueToSet) {
            try {
                @SuppressWarnings("unchecked")
                final R inner = (R) field.get(pojo);
                field.set(pojo, innerAccessor.set(inner, valueToSet));
                return pojo;
            } catch (IllegalAccessException iaex) {
                // The Field class is transient and when deserializing its value we also make it accessible
                throw new RuntimeException("This should not happen since we call setAccesssible(true) in readObject."
                        + " fields: " + field + " obj: " + pojo);
            }
        }

        private void writeObject(ObjectOutputStream out)
                throws IOException, ClassNotFoundException {
            out.defaultWriteObject();
            FieldSerializer.serializeField(field, out);
        }

        private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            field = FieldSerializer.deserializeField(in);
        }
    }

    static final class SimpleProductFieldAccessor<T, F> extends FieldAccessor<T, F> {

        private static final long serialVersionUID = 1L;

        private final int                    pos;
        private final TupleSerializerBase<T> serializer;
        private final Object[]               fields;
        private final int length;

        SimpleProductFieldAccessor(int pos, TypeInformation<T> typeInfo, ExecutionConfig config) {
            checkNotNull(typeInfo, "typeInfo must not be null.");
            int arity = ((TupleTypeInfoBase) typeInfo).getArity();
            if (pos < 0 || pos >= arity) {
                throw new CompositeType.InvalidFieldReferenceException(
                        "Tried to select " + ((Integer) pos).toString() + ". field on \"" +
                                typeInfo.toString() + "\", which is an invalid index.");
            }

            this.pos = pos;
            this.fieldType = ((TupleTypeInfoBase<T>) typeInfo).getTypeAt(pos);
            this.serializer = (TupleSerializerBase<T>) typeInfo.createSerializer(config);
            this.length = this.serializer.getArity();
            this.fields = new Object[this.length];
        }

        @SuppressWarnings("unchecked")
        @Override
        public F get(T record) {
            Product prod = (Product) record;
            return (F) prod.productElement(pos);
        }

        @Override
        public T set(T record, F fieldValue) {
            Product prod = (Product) record;
            for (int i = 0; i < length; i++) {
                fields[i] = prod.productElement(i);
            }
            fields[pos] = fieldValue;
            return serializer.createInstance(fields);
        }
    }

    static final class RecursiveProductFieldAccessor<T, R, F> extends FieldAccessor<T, F> {

        private static final long serialVersionUID = 1L;

        private final int pos;
        private final TupleSerializerBase<T> serializer;
        private final Object[] fields;
        private final int length;
        private final FieldAccessor<R, F> innerAccessor;

        RecursiveProductFieldAccessor(int pos, TypeInformation<T> typeInfo, FieldAccessor<R, F> innerAccessor, ExecutionConfig config) {
            int arity = ((TupleTypeInfoBase) typeInfo).getArity();
            if (pos < 0 || pos >= arity) {
                throw new CompositeType.InvalidFieldReferenceException(
                        "Tried to select " + ((Integer) pos).toString() + ". field on \"" +
                                typeInfo.toString() + "\", which is an invalid index.");
            }
            checkNotNull(typeInfo, "typeInfo must not be null.");
            checkNotNull(innerAccessor, "innerAccessor must not be null.");

            this.pos = pos;
            this.serializer = (TupleSerializerBase<T>) typeInfo.createSerializer(config);
            this.length = this.serializer.getArity();
            this.fields = new Object[this.length];
            this.innerAccessor = innerAccessor;
            this.fieldType = innerAccessor.getFieldType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public F get(T record) {
            return innerAccessor.get((R) ((Product) record).productElement(pos));
        }

        @SuppressWarnings("unchecked")
        @Override
        public T set(T record, F fieldValue) {
            Product prod = (Product) record;
            for (int i = 0; i < length; i++) {
                fields[i] = prod.productElement(i);
            }
            fields[pos] = innerAccessor.set((R) fields[pos], fieldValue);
            return serializer.createInstance(fields);
        }
    }
}
