package com.lxb.flink.runtime.plugable;

import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.core.io.IOReadableWritable;
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public class SerializationDelegate<T> implements IOReadableWritable {
    private T instance;

    private final TypeSerializer<T> serializer;

    public SerializationDelegate(TypeSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    public T getInstance() {
        return this.instance;
    }

    @Override
    public void write(DataOutputView out) throws IOException {
        this.serializer.serialize(this.instance, out);
    }

    @Override
    public void read(DataInputView in) throws IOException {
        throw new IllegalStateException("Deserialization method called on SerializationDelegate.");
    }
}
