package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public class ByteValue implements NormalizableKey<ByteValue>, ResettableValue<ByteValue>, CopyableValue<ByteValue> {
    private byte value;

    public ByteValue() {
        this.value = 0;
    }

    public ByteValue(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public void setValue(ByteValue value) {
        this.value = value.value;
    }

    @Override
    public void read(DataInputView in) throws IOException {
        this.value = in.readByte();
    }

    @Override
    public void write(DataOutputView out) throws IOException {
        out.writeByte(this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public int compareTo(ByteValue o) {
        final byte other = o.value;
        return this.value < other ? -1 : this.value > other ? 1 : 0;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ByteValue) {
            return ((ByteValue) obj).value == this.value;
        }
        return false;
    }

    @Override
    public int getMaxNormalizedKeyLen() {
        return 1;
    }

    @Override
    public void copyNormalizedKey(MemorySegment target, int offset, int len) {
        if (len == 1) {
            // default case, full normalized key. need to explicitly convert to int to
            // avoid false results due to implicit type conversion to int when subtracting
            // the min byte value
            int highByte = this.value & 0xff;
            highByte -= Byte.MIN_VALUE;
            target.put(offset, (byte) highByte);
        } else if (len > 1) {
            int highByte = this.value & 0xff;
            highByte -= Byte.MIN_VALUE;
            target.put(offset, (byte) highByte);
            for (int i = 1; i < len; i++) {
                target.put(offset + i, (byte) 0);
            }
        }
    }

    @Override
    public int getBinaryLength() {
        return 1;
    }

    @Override
    public void copyTo(ByteValue target) {
        target.value = this.value;
    }

    @Override
    public ByteValue copy() {
        return new ByteValue(this.value);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source, 1);
    }

}
