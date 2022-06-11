package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public final class VoidSerializer extends TypeSerializerSingleton<Void> {
    public static final VoidSerializer INSTANCE = new VoidSerializer();

    @Override
    public boolean isImmutableType() {
        return true;
    }

    @Override
    public Void createInstance() {
        return null;
    }

    @Override
    public Void copy(Void from) {
        return null;
    }

    @Override
    public Void copy(Void from, Void reuse) {
        return null;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public void serialize(Void record, DataOutputView target) throws IOException {
        // make progress in the stream, write one byte
        target.write(0);
    }

    @Override
    public Void deserialize(DataInputView source) throws IOException {
        source.readByte();
        return null;
    }

    @Override
    public Void deserialize(Void reuse, DataInputView source) throws IOException {
        source.readByte();
        return null;
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source.readByte());
    }
}
