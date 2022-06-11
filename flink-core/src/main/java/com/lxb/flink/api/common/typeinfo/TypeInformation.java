package com.lxb.flink.api.common.typeinfo;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.functions.InvalidTypesException;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.api.java.typeutils.TypeExtractor;
import com.lxb.flink.utl.FlinkRuntimeException;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public abstract class TypeInformation<T> implements Serializable {

    public abstract boolean isBasicType();

    public abstract boolean isTupleType();

    public abstract int getArity();

    public abstract int getTotalFields();

    public abstract Class<T> getTypeClass();

    public Map<String, TypeInformation<?>> getGenericParameters() {
        return Collections.emptyMap();
    }

    public abstract boolean isKeyType();

    public boolean isSortKeyType() {
        return isKeyType();
    }

    public abstract TypeSerializer<T> createSerializer(ExecutionConfig config);

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public abstract boolean canEqual(Object obj);

    public static <T> TypeInformation<T> of(Class<T> typeClass) {
        try {
            return TypeExtractor.createTypeInfo(typeClass);
        } catch (InvalidTypesException e) {
            throw new FlinkRuntimeException(
                    "Cannot extract TypeInformation from Class alone, because generic parameters are missing. " +
                            "Please use TypeInformation.of(TypeHint) instead, or another equivalent method in the API that " +
                            "accepts a TypeHint instead of a Class. " +
                            "For example for a Tuple2<Long, String> pass a 'new TypeHint<Tuple2<Long, String>>(){}'.");
        }
    }

    public static <T> TypeInformation<T> of(TypeHint<T> typeHint) {
        return typeHint.getTypeInfo();
    }


}
