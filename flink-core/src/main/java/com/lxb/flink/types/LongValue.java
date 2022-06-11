package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public class LongValue implements NormalizableKey<LongValue>, ResettableValue<LongValue>, CopyableValue<LongValue> {
    private long value;

    public LongValue() {
        this.value = 0;
    }

    public LongValue(final long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    @Override
    public void setValue(LongValue value) {
        this.value = value.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public void read(final DataInputView in) throws IOException {
        this.value = in.readLong();
    }

    @Override
    public void write(final DataOutputView out) throws IOException {
        out.writeLong(this.value);
    }

    @Override
    public int compareTo(LongValue o) {
        final long other = o.value;
        return this.value < other ? -1 : this.value > other ? 1 : 0;
    }

    @Override
    public int hashCode() {
        return 43 + (int) (this.value ^ this.value >>> 32);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof LongValue) {
            return ((LongValue) obj).value == this.value;
        }
        return false;
    }

    @Override
    public int getMaxNormalizedKeyLen() {
        return 8;
    }

    @Override
    public void copyNormalizedKey(MemorySegment target, int offset, int len) {
        // see IntValue for an explanation of the logic
        if (len == 8) {
            // default case, full normalized key
            target.putLongBigEndian(offset, value - Long.MIN_VALUE);
        } else if (len <= 0) {
        } else if (len < 8) {
            long value = this.value - Long.MIN_VALUE;
            for (int i = 0; len > 0; len--, i++) {
                target.put(offset + i, (byte) (value >>> ((7 - i) << 3)));
            }
        } else {
            target.putLongBigEndian(offset, value - Long.MIN_VALUE);
            for (int i = 8; i < len; i++) {
                target.put(offset + i, (byte) 0);
            }
        }
    }

    @Override
    public int getBinaryLength() {
        return 8;
    }

    @Override
    public void copyTo(LongValue target) {
        target.value = this.value;
    }

    @Override
    public LongValue copy() {
        return new LongValue(this.value);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source, 8);
    }

}
