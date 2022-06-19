package com.lxb.flink.util.typeutils;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.operators.Keys;
import com.lxb.flink.api.common.typeinfo.BasicTypeInfo;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.common.typeuitils.CompositeType;
import com.lxb.flink.api.java.typeutils.PojoField;
import com.lxb.flink.api.java.typeutils.PojoTypeInfo;
import com.lxb.flink.api.java.typeutils.TupleTypeInfo;
import com.lxb.flink.api.java.typeutils.TupleTypeInfoBase;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldAccessorFactory implements Serializable {
    public static <T, F> FieldAccessor<T, F> getAccessor(TypeInformation<T> typeInfo, int pos, ExecutionConfig config) {

        // In case of arrays
        if (typeInfo instanceof BasicTypeInfo) {
            if (pos != 0) {
                throw new CompositeType.InvalidFieldReferenceException("The " + ((Integer) pos).toString() + ". field selected on a " +
                        "basic type (" + typeInfo.toString() + "). A field expression on a basic type can only select " +
                        "the 0th field (which means selecting the entire basic type).");
            }
            @SuppressWarnings("unchecked")
            FieldAccessor<T, F> result = (FieldAccessor<T, F>) new FieldAccessor.SimpleFieldAccessor<>(typeInfo);
            return result;

            // In case of case classes
        } else if (typeInfo.isTupleType() && ((TupleTypeInfoBase) typeInfo).isCaseClass()) {
            TupleTypeInfoBase tupleTypeInfo = (TupleTypeInfoBase) typeInfo;
            @SuppressWarnings("unchecked")
            TypeInformation<F> fieldTypeInfo = (TypeInformation<F>) tupleTypeInfo.getTypeAt(pos);
            return new FieldAccessor.RecursiveProductFieldAccessor<>(
                    pos, typeInfo, new FieldAccessor.SimpleFieldAccessor<>(fieldTypeInfo), config);

            // In case of tuples
        } else if (typeInfo.isTupleType()) {
            @SuppressWarnings("unchecked")
            FieldAccessor<T, F> result = new FieldAccessor.SimpleTupleFieldAccessor(pos, typeInfo);
            return result;

            // Default case, PojoType is directed to this statement
        } else {
            throw new CompositeType.InvalidFieldReferenceException("Cannot reference field by position on " + typeInfo.toString()
                    + "Referencing a field by position is supported on tuples, case classes, and arrays. "
                    + "Additionally, you can select the 0th field of a primitive/basic type (e.g. int).");
        }
    }

    public static <T, F> FieldAccessor<T, F> getAccessor(TypeInformation<T> typeInfo, String field, ExecutionConfig config) {

        // In case of arrays
        if (typeInfo instanceof BasicTypeInfo) {
            try {
                int pos = field.equals(Keys.ExpressionKeys.SELECT_ALL_CHAR) ? 0 : Integer.parseInt(field);
                return FieldAccessorFactory.getAccessor(typeInfo, pos, config);
            } catch (NumberFormatException ex) {
                throw new CompositeType.InvalidFieldReferenceException("You tried to select the field \"" + field +
                        "\" on a " + typeInfo.toString() + ". A field expression on a basic type can only be \"*\" or \"0\"" +
                        " (both of which mean selecting the entire basic type).");
            }

            // In case of Pojos
        } else if (typeInfo instanceof PojoTypeInfo) {
            FieldExpression decomp       = decomposeFieldExpression(field);
            PojoTypeInfo<?> pojoTypeInfo = (PojoTypeInfo) typeInfo;

            int fieldIndex = pojoTypeInfo.getFieldIndex(decomp.head);

            if (fieldIndex == -1) {
                throw new CompositeType.InvalidFieldReferenceException(
                        "Unable to find field \"" + decomp.head + "\" in type " + typeInfo + ".");
            } else {
                PojoField          pojoField = pojoTypeInfo.getPojoFieldAt(fieldIndex);
                TypeInformation<?> fieldType = pojoTypeInfo.getTypeAt(fieldIndex);
                if (decomp.tail == null) {
                    @SuppressWarnings("unchecked")
                    FieldAccessor<F, F> innerAccessor = new FieldAccessor.SimpleFieldAccessor<>((TypeInformation<F>) fieldType);
                    return new FieldAccessor.PojoFieldAccessor<>(pojoField.getField(), innerAccessor);
                } else {
                    @SuppressWarnings("unchecked")
                    FieldAccessor<Object, F> innerAccessor = FieldAccessorFactory
                            .getAccessor((TypeInformation<Object>) fieldType, decomp.tail, config);
                    return new FieldAccessor.PojoFieldAccessor<>(pojoField.getField(), innerAccessor);
                }
            }
            // In case of case classes
        } else if (typeInfo.isTupleType() && ((TupleTypeInfoBase) typeInfo).isCaseClass()) {
            TupleTypeInfoBase tupleTypeInfo = (TupleTypeInfoBase) typeInfo;
            FieldExpression   decomp        = decomposeFieldExpression(field);
            int               fieldPos      = tupleTypeInfo.getFieldIndex(decomp.head);
            if (fieldPos < 0) {
                throw new CompositeType.InvalidFieldReferenceException("Invalid field selected: " + field);
            }

            if (decomp.tail == null) {
                return new FieldAccessor.SimpleProductFieldAccessor<>(fieldPos, typeInfo, config);
            } else {
                @SuppressWarnings("unchecked")
                FieldAccessor<Object, F> innerAccessor = getAccessor(tupleTypeInfo.getTypeAt(fieldPos), decomp.tail, config);
                return new FieldAccessor.RecursiveProductFieldAccessor<>(fieldPos, typeInfo, innerAccessor, config);
            }

            // In case of tuples
        } else if (typeInfo.isTupleType() && typeInfo instanceof TupleTypeInfo) {
            TupleTypeInfo   tupleTypeInfo = (TupleTypeInfo) typeInfo;
            FieldExpression decomp        = decomposeFieldExpression(field);
            int             fieldPos      = tupleTypeInfo.getFieldIndex(decomp.head);
            if (fieldPos == -1) {
                try {
                    fieldPos = Integer.parseInt(decomp.head);
                } catch (NumberFormatException ex) {
                    throw new CompositeType.InvalidFieldReferenceException("Tried to select field \"" + decomp.head
                            + "\" on " + typeInfo.toString() + " . Only integer values are allowed here.");
                }
            }
            if (decomp.tail == null) {
                @SuppressWarnings("unchecked")
                FieldAccessor<T, F> result = new FieldAccessor.SimpleTupleFieldAccessor(fieldPos, tupleTypeInfo);
                return result;
            } else {
                @SuppressWarnings("unchecked")
                FieldAccessor<?, F> innerAccessor = getAccessor(tupleTypeInfo.getTypeAt(fieldPos), decomp.tail, config);
                @SuppressWarnings("unchecked")
                FieldAccessor<T, F> result = new FieldAccessor.RecursiveTupleFieldAccessor(fieldPos, innerAccessor, tupleTypeInfo);
                return result;
            }

            // Default statement
        } else {
            throw new CompositeType.InvalidFieldReferenceException("Cannot reference field by field expression on " + typeInfo.toString()
                    + "Field expressions are only supported on POJO types, tuples, and case classes. "
                    + "(See the Flink documentation on what is considered a POJO.)");
        }
    }

    private static final String REGEX_FIELD                  = "[\\p{L}\\p{Digit}_\\$]*"; // This can start with a digit (because of Tuples)
    private static final String REGEX_NESTED_FIELDS          = "(" + REGEX_FIELD + ")(\\.(.+))?";
    private static final String REGEX_NESTED_FIELDS_WILDCARD = REGEX_NESTED_FIELDS
            + "|\\" + Keys.ExpressionKeys.SELECT_ALL_CHAR
            + "|\\" + Keys.ExpressionKeys.SELECT_ALL_CHAR_SCALA;

    private static final Pattern PATTERN_NESTED_FIELDS_WILDCARD = Pattern.compile(REGEX_NESTED_FIELDS_WILDCARD);

    private static FieldExpression decomposeFieldExpression(String fieldExpression) {
        Matcher matcher = PATTERN_NESTED_FIELDS_WILDCARD.matcher(fieldExpression);
        if (!matcher.matches()) {
            throw new CompositeType.InvalidFieldReferenceException("Invalid field expression \"" + fieldExpression + "\".");
        }

        String head = matcher.group(0);
        if (head.equals(Keys.ExpressionKeys.SELECT_ALL_CHAR) || head.equals(Keys.ExpressionKeys.SELECT_ALL_CHAR_SCALA)) {
            throw new CompositeType.InvalidFieldReferenceException("No wildcards are allowed here.");
        } else {
            head = matcher.group(1);
        }

        String tail = matcher.group(3);

        return new FieldExpression(head, tail);
    }

    private static class FieldExpression implements Serializable {

        private static final long serialVersionUID = 1L;

        public String head, tail; // tail can be null, if the field expression had just one part

        FieldExpression(String head, String tail) {
            this.head = head;
            this.tail = tail;
        }
    }

}
