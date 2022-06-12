package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public final class LongSerializer extends TypeSerializerSingleton<Long> {
    public static final LongSerializer INSTANCE = new LongSerializer();

    private static final Long ZERO = 0L;

    @Override
    public boolean isImmutableType() {
        return true;
    }

    @Override
    public Long createInstance() {
        return ZERO;
    }

    @Override
    public Long copy(Long from) {
        return from;
    }

    @Override
    public Long copy(Long from, Long reuse) {
        return from;
    }

    @Override
    public int getLength() {
        return Long.BYTES;
    }

    @Override
    public void serialize(Long record, DataOutputView target) throws IOException {
        target.writeLong(record);
    }

    @Override
    public Long deserialize(DataInputView source) throws IOException {
        return source.readLong();
    }

    @Override
    public Long deserialize(Long reuse, DataInputView source) throws IOException {
        return deserialize(source);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeLong(source.readLong());
    }
}
