package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public class ShortValue implements NormalizableKey<ShortValue>, ResettableValue<ShortValue>, CopyableValue<ShortValue> {
    private short value;
    public ShortValue() {
        this.value = 0;
    }
    public ShortValue(short value) {
        this.value = value;
    }
    public short getValue() {
        return this.value;
    }
    public void setValue(short value) {
        this.value = value;
    }
    @Override
    public void setValue(ShortValue value) {
        this.value = value.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
    @Override
    public void read(DataInputView in) throws IOException {
        this.value = in.readShort();
    }

    @Override
    public void write(DataOutputView out) throws IOException {
        out.writeShort(this.value);
    }
    @Override
    public int compareTo(ShortValue o) {
        final int other = o.value;
        return this.value < other ? -1 : this.value > other ? 1 : 0;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ShortValue) {
            return ((ShortValue) obj).value == this.value;
        }
        return false;
    }

    @Override
    public int getMaxNormalizedKeyLen() {
        return 2;
    }

    @Override
    public void copyNormalizedKey(MemorySegment target, int offset, int len) {
        if (len == 2) {
            // default case, full normalized key
            int highByte = ((value >>> 8) & 0xff);
            highByte -= Byte.MIN_VALUE;
            target.put(offset, (byte) highByte);
            target.put(offset + 1, (byte) ((value) & 0xff));
        }
        else if (len <= 0) {
        }
        else if (len == 1) {
            int highByte = ((value >>> 8) & 0xff);
            highByte -= Byte.MIN_VALUE;
            target.put(offset, (byte) highByte);
        }
        else {
            int highByte = ((value >>> 8) & 0xff);
            highByte -= Byte.MIN_VALUE;
            target.put(offset, (byte) highByte);
            target.put(offset + 1, (byte) ((value) & 0xff));
            for (int i = 2; i < len; i++) {
                target.put(offset + i, (byte) 0);
            }
        }
    }
    @Override
    public int getBinaryLength() {
        return 2;
    }

    @Override
    public void copyTo(ShortValue target) {
        target.value = this.value;
    }

    @Override
    public ShortValue copy() {
        return new ShortValue(this.value);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source, 2);
    }

}
