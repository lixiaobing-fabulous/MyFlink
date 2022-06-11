package com.lxb.flink.api.common.typeinfo;

import com.lxb.flink.api.common.functions.InvalidTypesException;
import com.lxb.flink.api.java.typeutils.TypeExtractor;
import com.lxb.flink.utl.FlinkRuntimeException;

public abstract class TypeHint<T> {
    private final TypeInformation<T> typeInfo;

    public TypeHint() {
        try {
            this.typeInfo = TypeExtractor.createTypeInfo(
                    this, TypeHint.class, getClass(), 0);
        } catch (InvalidTypesException e) {
            throw new FlinkRuntimeException("The TypeHint is using a generic variable." +
                    "This is not supported, generic types must be fully specified for the TypeHint.");
        }
    }

    public TypeInformation<T> getTypeInfo() {
        return typeInfo;
    }

    @Override
    public int hashCode() {
        return typeInfo.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                obj instanceof TypeHint && this.typeInfo.equals(((TypeHint<?>) obj).typeInfo);
    }

    @Override
    public String toString() {
        return "TypeHint: " + typeInfo;
    }

}
