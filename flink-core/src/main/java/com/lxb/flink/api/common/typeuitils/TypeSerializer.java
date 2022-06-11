package com.lxb.flink.api.common.typeuitils;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;
import java.io.Serializable;

public abstract class TypeSerializer<T> implements Serializable {
    public abstract boolean isImmutableType();

    public abstract TypeSerializer<T> duplicate();

    public abstract T createInstance();

    public abstract T copy(T from);

    public abstract T copy(T from, T reuse);

    public abstract int getLength();

    public abstract void serialize(T record, DataOutputView target) throws IOException;

    public abstract T deserialize(DataInputView source) throws IOException;

    public abstract T deserialize(T reuse, DataInputView source) throws IOException;

    public abstract void copy(DataInputView source, DataOutputView target) throws IOException;

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

}
