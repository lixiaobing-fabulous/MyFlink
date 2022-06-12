package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.types.LongValue;

import java.io.IOException;

public final class LongValueSerializer extends TypeSerializerSingleton<LongValue> {
    public static final LongValueSerializer INSTANCE = new LongValueSerializer();

    @Override
    public boolean isImmutableType() {
        return false;
    }

    @Override
    public LongValue createInstance() {
        return new LongValue();
    }

    @Override
    public LongValue copy(LongValue from) {
        return copy(from, new LongValue());
    }

    @Override
    public LongValue copy(LongValue from, LongValue reuse) {
        reuse.setValue(from.getValue());
        return reuse;
    }

    @Override
    public int getLength() {
        return 8;
    }

    @Override
    public void serialize(LongValue record, DataOutputView target) throws IOException {
        record.write(target);
    }

    @Override
    public LongValue deserialize(DataInputView source) throws IOException {
        return deserialize(new LongValue(), source);
    }

    @Override
    public LongValue deserialize(LongValue reuse, DataInputView source) throws IOException {
        reuse.read(source);
        return reuse;
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeLong(source.readLong());
    }
}
