package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public final class IntSerializer extends TypeSerializerSingleton<Integer> {
    public static final IntSerializer INSTANCE = new IntSerializer();

    private static final Integer ZERO = 0;

    @Override
    public boolean isImmutableType() {
        return true;
    }

    @Override
    public Integer createInstance() {
        return ZERO;
    }

    @Override
    public Integer copy(Integer from) {
        return from;
    }

    @Override
    public Integer copy(Integer from, Integer reuse) {
        return from;
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public void serialize(Integer record, DataOutputView target) throws IOException {
        target.writeInt(record);
    }

    @Override
    public Integer deserialize(DataInputView source) throws IOException {
        return source.readInt();
    }

    @Override
    public Integer deserialize(Integer reuse, DataInputView source) throws IOException {
        return deserialize(source);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeInt(source.readInt());
    }
}
