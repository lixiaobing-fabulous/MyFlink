package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public class CharSerializer extends TypeSerializerSingleton<Character> {
    public static final  CharSerializer INSTANCE = new CharSerializer();
    private static final Character      ZERO     = (char) 0;

    @Override
    public boolean isImmutableType() {
        return true;
    }

    @Override
    public Character createInstance() {
        return ZERO;
    }

    @Override
    public Character copy(Character from) {
        return from;
    }

    @Override
    public Character copy(Character from, Character reuse) {
        return from;
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public void serialize(Character record, DataOutputView target) throws IOException {
        target.writeChar(record);
    }

    @Override
    public Character deserialize(DataInputView source) throws IOException {
        return source.readChar();
    }

    @Override
    public Character deserialize(Character reuse, DataInputView source) throws IOException {
        return deserialize(source);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.writeChar(source.readChar());
    }
}
