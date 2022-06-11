package com.lxb.flink.api.common.typeuitils;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeinfo.AtomicType;
import com.lxb.flink.api.common.typeinfo.TypeInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

public abstract class CompositeType<T> extends TypeInformation<T> {
    private final Class<T> typeClass;

    public CompositeType(Class<T> typeClass) {
        this.typeClass = checkNotNull(typeClass);
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public List<FlatFieldDescriptor> getFlatFields(String fieldExpression) {
        List<FlatFieldDescriptor> result = new ArrayList<FlatFieldDescriptor>();
        this.getFlatFields(fieldExpression, 0, result);
        return result;
    }

    public abstract void getFlatFields(String fieldExpression, int offset, List<FlatFieldDescriptor> result);

    public abstract <X> TypeInformation<X> getTypeAt(String fieldExpression);

    public abstract <X> TypeInformation<X> getTypeAt(int pos);

    protected abstract TypeComparatorBuilder<T> createTypeComparatorBuilder();


    public TypeComparator<T> createComparator(int[] logicalKeyFields, boolean[] orders, int logicalFieldOffset, ExecutionConfig config) {

        TypeComparatorBuilder<T> builder = createTypeComparatorBuilder();

        builder.initializeTypeComparatorBuilder(logicalKeyFields.length);

        for (int logicalKeyFieldIndex = 0; logicalKeyFieldIndex < logicalKeyFields.length; logicalKeyFieldIndex++) {
            int     logicalKeyField = logicalKeyFields[logicalKeyFieldIndex];
            int     logicalField    = logicalFieldOffset; // this is the global/logical field number
            boolean comparatorAdded = false;

            for (int localFieldId = 0; localFieldId < this.getArity() && logicalField <= logicalKeyField && !comparatorAdded; localFieldId++) {
                TypeInformation<?> localFieldType = this.getTypeAt(localFieldId);

                if (localFieldType instanceof AtomicType && logicalField == logicalKeyField) {
                    // we found an atomic key --> create comparator
                    builder.addComparatorField(
                            localFieldId,
                            ((AtomicType<?>) localFieldType).createComparator(
                                    orders[logicalKeyFieldIndex],
                                    config));

                    comparatorAdded = true;
                }
                // must be composite type and check that the logicalKeyField is within the bounds
                // of the composite type's logical fields
                else if (localFieldType instanceof CompositeType &&
                        logicalField <= logicalKeyField &&
                        logicalKeyField <= logicalField + (localFieldType.getTotalFields() - 1)) {
                    // we found a compositeType that is containing the logicalKeyField we are looking for --> create comparator
                    builder.addComparatorField(
                            localFieldId,
                            ((CompositeType<?>) localFieldType).createComparator(
                                    new int[]{logicalKeyField},
                                    new boolean[]{orders[logicalKeyFieldIndex]},
                                    logicalField,
                                    config)
                    );

                    comparatorAdded = true;
                }

                if (localFieldType instanceof CompositeType) {
                    // we need to subtract 1 because we are not accounting for the local field (not accessible for the user)
                    logicalField += localFieldType.getTotalFields() - 1;
                }

                logicalField++;
            }

            if (!comparatorAdded) {
                throw new IllegalArgumentException("Could not add a comparator for the logical" +
                        "key field index " + logicalKeyFieldIndex + ".");
            }
        }

        return builder.createTypeComparator(config);
    }

    protected interface TypeComparatorBuilder<T> {
        void initializeTypeComparatorBuilder(int size);

        void addComparatorField(int fieldId, TypeComparator<?> comparator);

        TypeComparator<T> createTypeComparator(ExecutionConfig config);
    }

    public static class FlatFieldDescriptor {
        private int                keyPosition;
        private TypeInformation<?> type;

        public FlatFieldDescriptor(int keyPosition, TypeInformation<?> type) {
            if (type instanceof CompositeType) {
                throw new IllegalArgumentException("A flattened field can not be a composite type");
            }
            this.keyPosition = keyPosition;
            this.type = type;
        }


        public int getPosition() {
            return keyPosition;
        }

        public TypeInformation<?> getType() {
            return type;
        }

        @Override
        public String toString() {
            return "FlatFieldDescriptor [position=" + keyPosition + " typeInfo=" + type + "]";
        }
    }

    public boolean hasField(String fieldName) {
        return getFieldIndex(fieldName) >= 0;
    }

    @Override
    public boolean isKeyType() {
        for (int i = 0; i < this.getArity(); i++) {
            if (!this.getTypeAt(i).isKeyType()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSortKeyType() {
        for (int i = 0; i < this.getArity(); i++) {
            if (!this.getTypeAt(i).isSortKeyType()) {
                return false;
            }
        }
        return true;
    }

    public abstract String[] getFieldNames();

    public boolean hasDeterministicFieldOrder() {
        return false;
    }

    public abstract int getFieldIndex(String fieldName);

    public static class InvalidFieldReferenceException extends IllegalArgumentException {

        private static final long serialVersionUID = 1L;

        public InvalidFieldReferenceException(String s) {
            super(s);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CompositeType) {
            @SuppressWarnings("unchecked")
            CompositeType<T> compositeType = (CompositeType<T>) obj;

            return compositeType.canEqual(this) && typeClass == compositeType.typeClass;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeClass);
    }

    @Override
    public boolean canEqual(Object obj) {
        return obj instanceof CompositeType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + typeClass.getSimpleName() + ">";
    }

}
