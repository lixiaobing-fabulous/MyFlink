package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.types.StringValue;

import java.io.IOException;

public class StringSerializer extends TypeSerializerSingleton<String> {
    public static final  StringSerializer INSTANCE = new StringSerializer();
    private static final String           EMPTY    = "";

    @Override
    public boolean isImmutableType() {
        return true;
    }

    @Override
    public String createInstance() {
        return EMPTY;
    }

    @Override
    public String copy(String from) {
        return from;
    }

    @Override
    public String copy(String from, String reuse) {
        return from;
    }

    @Override
    public int getLength() {
        return -1;
    }

    @Override
    public void serialize(String record, DataOutputView target) throws IOException {
        StringValue.writeString(record, target);
    }

    @Override
    public String deserialize(DataInputView source) throws IOException {
        return StringValue.readString(source);
    }

    @Override
    public String deserialize(String record, DataInputView source) throws IOException {
        return deserialize(source);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        StringValue.copyString(source, target);
    }


}
