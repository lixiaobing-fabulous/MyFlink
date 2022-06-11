package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public final class BooleanSerializer extends TypeSerializerSingleton<Boolean> {
    public static final BooleanSerializer INSTANCE = new BooleanSerializer();

    private static final Boolean FALSE = Boolean.FALSE;
    @Override
    public boolean isImmutableType() {
        return true;
    }

    @Override
    public Boolean createInstance() {
        return FALSE;
    }

    @Override
    public Boolean copy(Boolean from) {
        return from;
    }

    @Override
    public Boolean copy(Boolean from, Boolean reuse) {
        return from;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public void serialize(Boolean record, DataOutputView target) throws IOException {
        target.writeBoolean(record);
    }

    @Override
    public Boolean deserialize(DataInputView source) throws IOException {
        return source.readBoolean();
    }

    @Override
    public Boolean deserialize(Boolean reuse, DataInputView source) throws IOException {
        return source.readBoolean();
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeBoolean(source.readBoolean());
    }
}
