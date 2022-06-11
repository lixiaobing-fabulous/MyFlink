package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public class IntValue implements NormalizableKey<IntValue>, ResettableValue<IntValue>, CopyableValue<IntValue> {
    private int value;

    public IntValue() {
        this.value = 0;
    }

    public IntValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void setValue(IntValue value) {
        this.value = value.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public void read(DataInputView in) throws IOException {
        this.value = in.readInt();
    }

    @Override
    public void write(DataOutputView out) throws IOException {
        out.writeInt(this.value);
    }

    @Override
    public int compareTo(IntValue o) {
        final int other = o.value;
        return this.value < other ? -1 : this.value > other ? 1 : 0;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IntValue) {
            return ((IntValue) obj).value == this.value;
        }
        return false;
    }

    @Override
    public int getMaxNormalizedKeyLen() {
        return 4;
    }

    @Override
    public void copyNormalizedKey(MemorySegment target, int offset, int len) {
        // take out value and add the integer min value. This gets an offset
        // representation when interpreted as an unsigned integer (as is the case
        // with normalized keys). write this value as big endian to ensure the
        // most significant byte comes first.
        if (len == 4) {
            target.putIntBigEndian(offset, value - Integer.MIN_VALUE);
        } else if (len <= 0) {
        } else if (len < 4) {
            int value = this.value - Integer.MIN_VALUE;
            for (int i = 0; len > 0; len--, i++) {
                target.put(offset + i, (byte) ((value >>> ((3 - i) << 3)) & 0xff));
            }
        } else {
            target.putIntBigEndian(offset, value - Integer.MIN_VALUE);
            for (int i = 4; i < len; i++) {
                target.put(offset + i, (byte) 0);
            }
        }
    }

    @Override
    public int getBinaryLength() {
        return 4;
    }

    @Override
    public void copyTo(IntValue target) {
        target.value = this.value;
    }

    @Override
    public IntValue copy() {
        return new IntValue(this.value);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source, 4);
    }

}
