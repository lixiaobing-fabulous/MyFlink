package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.types.CharValue;

import java.io.IOException;

public class CharValueSerializer extends TypeSerializerSingleton<CharValue> {
    public static final CharValueSerializer INSTANCE = new CharValueSerializer();

    @Override
    public boolean isImmutableType() {
        return false;
    }

    @Override
    public CharValue createInstance() {
        return new CharValue();
    }

    @Override
    public CharValue copy(CharValue from) {
        return copy(from, new CharValue());
    }

    @Override
    public CharValue copy(CharValue from, CharValue reuse) {
        reuse.setValue(from.getValue());
        return reuse;
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public void serialize(CharValue record, DataOutputView target) throws IOException {
        record.write(target);
    }

    @Override
    public CharValue deserialize(DataInputView source) throws IOException {
        return deserialize(new CharValue(), source);
    }

    @Override
    public CharValue deserialize(CharValue reuse, DataInputView source) throws IOException {
        reuse.read(source);
        return reuse;
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeChar(source.readChar());
    }

}
