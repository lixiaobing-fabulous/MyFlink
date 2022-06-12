package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.types.IntValue;

import java.io.IOException;

public final class IntValueSerializer extends TypeSerializerSingleton<IntValue> {
    public static final IntValueSerializer INSTANCE = new IntValueSerializer();

    @Override
    public boolean isImmutableType() {
        return false;
    }

    @Override
    public IntValue createInstance() {
        return new IntValue();
    }

    @Override
    public IntValue copy(IntValue from) {
        return copy(from, new IntValue());
    }

    @Override
    public IntValue copy(IntValue from, IntValue reuse) {
        reuse.setValue(from.getValue());
        return reuse;
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public void serialize(IntValue record, DataOutputView target) throws IOException {
        record.write(target);
    }

    @Override
    public IntValue deserialize(DataInputView source) throws IOException {
        return deserialize(new IntValue(), source);
    }

    @Override
    public IntValue deserialize(IntValue reuse, DataInputView source) throws IOException {
        reuse.read(source);
        return reuse;
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeInt(source.readInt());
    }
}
