package com.lxb.flink.api.common.typeuitils.base;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.types.StringValue;

import java.io.IOException;

public class StringValueSerializer extends TypeSerializerSingleton<StringValue> {
    private static final int HIGH_BIT = 0x1 << 7;

    public static final StringValueSerializer INSTANCE = new StringValueSerializer();

    @Override
    public boolean isImmutableType() {
        return false;
    }

    @Override
    public StringValue createInstance() {
        return new StringValue();
    }

    @Override
    public StringValue copy(StringValue from) {
        return copy(from, new StringValue());
    }

    @Override
    public StringValue copy(StringValue from, StringValue reuse) {
        reuse.setValue(from);
        return reuse;
    }

    @Override
    public int getLength() {
        return -1;
    }

    @Override
    public void serialize(StringValue record, DataOutputView target) throws IOException {
        record.write(target);
    }

    @Override
    public StringValue deserialize(DataInputView source) throws IOException {
        return deserialize(new StringValue(), source);
    }

    @Override
    public StringValue deserialize(StringValue reuse, DataInputView source) throws IOException {
        reuse.read(source);
        return reuse;
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        int len = source.readUnsignedByte();
        target.writeByte(len);

        if (len >= HIGH_BIT) {
            int shift = 7;
            int curr;
            len = len & 0x7f;
            while ((curr = source.readUnsignedByte()) >= HIGH_BIT) {
                target.writeByte(curr);
                len |= (curr & 0x7f) << shift;
                shift += 7;
            }
            target.writeByte(curr);
            len |= curr << shift;
        }

        for (int i = 0; i < len; i++) {
            int c = source.readUnsignedByte();
            target.writeByte(c);
            while (c >= HIGH_BIT) {
                c = source.readUnsignedByte();
                target.writeByte(c);
            }
        }
    }

}
