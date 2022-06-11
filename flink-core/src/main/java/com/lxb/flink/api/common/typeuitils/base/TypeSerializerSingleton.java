package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.api.common.typeuitils.TypeSerializer;

public abstract class TypeSerializerSingleton<T> extends TypeSerializer<T> {
    @Override
    public TypeSerializerSingleton<T> duplicate() {
        return this;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass());
    }

}
