/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lxb.flink.api.common.operators;

import com.lxb.flink.annotation.Internal;
import com.lxb.flink.api.common.InvalidProgramException;
import com.lxb.flink.api.common.functions.Partitioner;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.common.typeuitils.CompositeType;
import com.lxb.flink.api.java.typeutils.TypeExtractor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.lxb.flink.utl.Preconditions.checkArgument;
import static com.lxb.flink.utl.Preconditions.checkNotNull;

@Internal
public abstract class Keys<T> {

    public abstract int getNumberOfKeyFields();

    public abstract int[] computeLogicalKeyPositions();

    public abstract TypeInformation<?>[] getKeyFieldTypes();

    public abstract TypeInformation<?>[] getOriginalKeyFieldTypes();

    public abstract <E> void validateCustomPartitioner(Partitioner<E> partitioner, TypeInformation<E> typeInfo);

    public boolean isEmpty() {
        return getNumberOfKeyFields() == 0;
    }

    /**
     * Check if two sets of keys are compatible to each other (matching types, key counts)
     */
    public boolean areCompatible(Keys<?> other) throws IncompatibleKeysException {

        TypeInformation<?>[] thisKeyFieldTypes  = this.getKeyFieldTypes();
        TypeInformation<?>[] otherKeyFieldTypes = other.getKeyFieldTypes();

        if (thisKeyFieldTypes.length != otherKeyFieldTypes.length) {
            throw new IncompatibleKeysException(IncompatibleKeysException.SIZE_MISMATCH_MESSAGE);
        } else {
            for (int i = 0; i < thisKeyFieldTypes.length; i++) {
                if (!thisKeyFieldTypes[i].equals(otherKeyFieldTypes[i])) {
                    throw new IncompatibleKeysException(thisKeyFieldTypes[i], otherKeyFieldTypes[i]);
                }
            }
        }
        return true;
    }

    // --------------------------------------------------------------------------------------------
    //  Specializations for expression-based / extractor-based grouping
    // --------------------------------------------------------------------------------------------


    /**
     * Represents (nested) field access through string and integer-based keys
     */
    public static class ExpressionKeys<T> extends Keys<T> {

        public static final  String  SELECT_ALL_CHAR       = "*";
        public static final  String  SELECT_ALL_CHAR_SCALA = "_";
        private static final Pattern WILD_CARD_REGEX       = Pattern.compile("[\\.]?("
                + "\\" + SELECT_ALL_CHAR + "|"
                + "\\" + SELECT_ALL_CHAR_SCALA + ")$");

        // Flattened fields representing keys fields
        private List<CompositeType.FlatFieldDescriptor> keyFields;
        private TypeInformation<?>[]                    originalKeyTypes;

        /**
         * ExpressionKeys that is defined by the full data type.
         */
        public ExpressionKeys(TypeInformation<T> type) {
            this(SELECT_ALL_CHAR, type);
        }

        /**
         * Create int-based (non-nested) field position keys on a tuple type.
         */
        public ExpressionKeys(int keyPosition, TypeInformation<T> type) {
            this(new int[]{keyPosition}, type, false);
        }

        /**
         * Create int-based (non-nested) field position keys on a tuple type.
         */
        public ExpressionKeys(int[] keyPositions, TypeInformation<T> type) {
            this(keyPositions, type, false);
        }

        /**
         * Create int-based (non-nested) field position keys on a tuple type.
         */
        public ExpressionKeys(int[] keyPositions, TypeInformation<T> type, boolean allowEmpty) {

            if (!type.isTupleType() || !(type instanceof CompositeType)) {
                throw new InvalidProgramException("Specifying keys via field positions is only valid " +
                        "for tuple data types. Type: " + type);
            }
            if (type.getArity() == 0) {
                throw new InvalidProgramException("Tuple size must be greater than 0. Size: " + type.getArity());
            }
            if (!allowEmpty && (keyPositions == null || keyPositions.length == 0)) {
                throw new IllegalArgumentException("The grouping fields must not be empty.");
            }

            this.keyFields = new ArrayList<>();

            if (keyPositions == null || keyPositions.length == 0) {
                // use all tuple fields as key fields
                keyPositions = createIncrIntArray(type.getArity());
            } else {
                rangeCheckFields(keyPositions, type.getArity() - 1);
            }

            checkArgument(keyPositions.length > 0, "Grouping fields can not be empty at this point");

            // extract key field types
            CompositeType<T> cType = (CompositeType<T>) type;
            this.keyFields = new ArrayList<>(type.getTotalFields());

            // for each key position, find all (nested) field types
            String[] fieldNames = cType.getFieldNames();
            this.originalKeyTypes = new TypeInformation<?>[keyPositions.length];
            ArrayList<CompositeType.FlatFieldDescriptor> tmpList = new ArrayList<>();
            for (int i = 0; i < keyPositions.length; i++) {
                int keyPos = keyPositions[i];
                tmpList.clear();
                // get all flat fields
                this.originalKeyTypes[i] = cType.getTypeAt(keyPos);
                cType.getFlatFields(fieldNames[keyPos], 0, tmpList);
                // check if fields are of key type
                for (CompositeType.FlatFieldDescriptor ffd : tmpList) {
                    if (!ffd.getType().isKeyType()) {
                        throw new InvalidProgramException("This type (" + ffd.getType() + ") cannot be used as key.");
                    }
                }
                this.keyFields.addAll(tmpList);
            }
        }

        /**
         * Create String-based (nested) field expression keys on a composite type.
         */
        public ExpressionKeys(String keyExpression, TypeInformation<T> type) {
            this(new String[]{keyExpression}, type);
        }

        /**
         * Create String-based (nested) field expression keys on a composite type.
         */
        public ExpressionKeys(String[] keyExpressions, TypeInformation<T> type) {
            checkNotNull(keyExpressions, "Field expression cannot be null.");

            this.keyFields = new ArrayList<>(keyExpressions.length);

            if (type instanceof CompositeType) {
                CompositeType<T> cType = (CompositeType<T>) type;
                this.originalKeyTypes = new TypeInformation<?>[keyExpressions.length];

                // extract the keys on their flat position
                for (int i = 0; i < keyExpressions.length; i++) {
                    String keyExpr = keyExpressions[i];

                    if (keyExpr == null) {
                        throw new InvalidProgramException("Expression key may not be null.");
                    }
                    // strip off whitespace
                    keyExpr = keyExpr.trim();

                    List<CompositeType.FlatFieldDescriptor> flatFields = cType.getFlatFields(keyExpr);

                    if (flatFields.size() == 0) {
                        throw new InvalidProgramException("Unable to extract key from expression '" + keyExpr + "' on key " + cType);
                    }
                    // check if all nested fields can be used as keys
                    for (CompositeType.FlatFieldDescriptor field : flatFields) {
                        if (!field.getType().isKeyType()) {
                            throw new InvalidProgramException("This type (" + field.getType() + ") cannot be used as key.");
                        }
                    }
                    // add flat fields to key fields
                    keyFields.addAll(flatFields);

                    String strippedKeyExpr = WILD_CARD_REGEX.matcher(keyExpr).replaceAll("");
                    if (strippedKeyExpr.isEmpty()) {
                        this.originalKeyTypes[i] = type;
                    } else {
                        this.originalKeyTypes[i] = cType.getTypeAt(strippedKeyExpr);
                    }
                }
            } else {
                if (!type.isKeyType()) {
                    throw new InvalidProgramException("This type (" + type + ") cannot be used as key.");
                }

                // check that all key expressions are valid
                for (String keyExpr : keyExpressions) {
                    if (keyExpr == null) {
                        throw new InvalidProgramException("Expression key may not be null.");
                    }
                    // strip off whitespace
                    keyExpr = keyExpr.trim();
                    // check that full type is addressed
                    if (!(SELECT_ALL_CHAR.equals(keyExpr) || SELECT_ALL_CHAR_SCALA.equals(keyExpr))) {
                        throw new InvalidProgramException(
                                "Field expression must be equal to '" + SELECT_ALL_CHAR + "' or '" + SELECT_ALL_CHAR_SCALA + "' for non-composite types.");
                    }
                    // add full type as key
                    keyFields.add(new CompositeType.FlatFieldDescriptor(0, type));
                }
                this.originalKeyTypes = new TypeInformation[]{type};
            }
        }

        @Override
        public int getNumberOfKeyFields() {
            if (keyFields == null) {
                return 0;
            }
            return keyFields.size();
        }

        @Override
        public int[] computeLogicalKeyPositions() {
            int[] logicalKeys = new int[keyFields.size()];
            for (int i = 0; i < keyFields.size(); i++) {
                logicalKeys[i] = keyFields.get(i).getPosition();
            }
            return logicalKeys;
        }

        @Override
        public TypeInformation<?>[] getKeyFieldTypes() {
            TypeInformation<?>[] fieldTypes = new TypeInformation[keyFields.size()];
            for (int i = 0; i < keyFields.size(); i++) {
                fieldTypes[i] = keyFields.get(i).getType();
            }
            return fieldTypes;
        }

        @Override
        public TypeInformation<?>[] getOriginalKeyFieldTypes() {
            return originalKeyTypes;
        }

        @Override
        public <E> void validateCustomPartitioner(Partitioner<E> partitioner, TypeInformation<E> typeInfo) {

            if (keyFields.size() != 1) {
                throw new InvalidProgramException("Custom partitioners can only be used with keys that have one key field.");
            }

            if (typeInfo == null) {
                // try to extract key type from partitioner
                try {
                    typeInfo = TypeExtractor.getPartitionerTypes(partitioner);
                } catch (Throwable t) {
                    // best effort check, so we ignore exceptions
                }
            }

            if (typeInfo != null) {
                // only check type compatibility if type is known and not a generic type

                TypeInformation<?> keyType = keyFields.get(0).getType();
                if (!keyType.equals(typeInfo)) {
                    throw new InvalidProgramException("The partitioner is incompatible with the key type. "
                            + "Partitioner type: " + typeInfo + " , key type: " + keyType);
                }
            }
        }

        @Override
        public String toString() {
            return "ExpressionKeys: " + StringUtils.join(keyFields, '.');
        }

        public static boolean isSortKey(int fieldPos, TypeInformation<?> type) {

            if (!type.isTupleType() || !(type instanceof CompositeType)) {
                throw new InvalidProgramException("Specifying keys via field positions is only valid " +
                        "for tuple data types. Type: " + type);
            }
            if (type.getArity() == 0) {
                throw new InvalidProgramException("Tuple size must be greater than 0. Size: " + type.getArity());
            }

            if (fieldPos < 0 || fieldPos >= type.getArity()) {
                throw new IndexOutOfBoundsException("Tuple position is out of range: " + fieldPos);
            }

            TypeInformation<?> sortKeyType = ((CompositeType<?>) type).getTypeAt(fieldPos);
            return sortKeyType.isSortKeyType();
        }

        public static boolean isSortKey(String fieldExpr, TypeInformation<?> type) {

            TypeInformation<?> sortKeyType;

            fieldExpr = fieldExpr.trim();
            if (SELECT_ALL_CHAR.equals(fieldExpr) || SELECT_ALL_CHAR_SCALA.equals(fieldExpr)) {
                sortKeyType = type;
            } else {
                if (type instanceof CompositeType) {
                    sortKeyType = ((CompositeType<?>) type).getTypeAt(fieldExpr);
                } else {
                    throw new InvalidProgramException(
                            "Field expression must be equal to '" + SELECT_ALL_CHAR + "' or '" + SELECT_ALL_CHAR_SCALA + "' for atomic types.");
                }
            }

            return sortKeyType.isSortKeyType();
        }

    }

    // --------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------
    //  Utilities
    // --------------------------------------------------------------------------------------------


    private static int[] createIncrIntArray(int numKeys) {
        int[] keyFields = new int[numKeys];
        for (int i = 0; i < numKeys; i++) {
            keyFields[i] = i;
        }
        return keyFields;
    }

    private static void rangeCheckFields(int[] fields, int maxAllowedField) {

        for (int f : fields) {
            if (f < 0 || f > maxAllowedField) {
                throw new IndexOutOfBoundsException("Tuple position is out of range: " + f);
            }
        }
    }

    public static class IncompatibleKeysException extends Exception {
        private static final long   serialVersionUID      = 1L;
        public static final  String SIZE_MISMATCH_MESSAGE = "The number of specified keys is different.";

        public IncompatibleKeysException(String message) {
            super(message);
        }

        public IncompatibleKeysException(TypeInformation<?> typeInformation, TypeInformation<?> typeInformation2) {
            super(typeInformation + " and " + typeInformation2 + " are not compatible");
        }
    }
}
