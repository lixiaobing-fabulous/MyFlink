package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.core.memory.MemorySegment;

import java.io.IOException;

public class CharValue implements NormalizableKey<CharValue>, ResettableValue<CharValue>, CopyableValue<CharValue> {
    private char value;

    public CharValue() {
        this.value = 0;
    }

    public CharValue(char value) {
        this.value = value;
    }

    public char getValue() {
        return this.value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    @Override
    public void setValue(CharValue value) {
        this.value = value.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public void read(DataInputView in) throws IOException {
        this.value = in.readChar();
    }

    @Override
    public void write(DataOutputView out) throws IOException {
        out.writeChar(this.value);
    }

    @Override
    public int compareTo(CharValue o) {
        final int other = o.value;
        return this.value < other ? -1 : this.value > other ? 1 : 0;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CharValue) {
            return ((CharValue) obj).value == this.value;
        }
        return false;
    }

    @Override
    public int getMaxNormalizedKeyLen() {
        return 2;
    }

    @Override
    public void copyNormalizedKey(MemorySegment target, int offset, int len) {
        // note that the char is an unsigned data type in java and consequently needs
        // no code that transforms the signed representation to an offset representation
        // that is equivalent to unsigned, when compared byte by byte
        if (len == 2) {
            // default case, full normalized key
            target.put(offset, (byte) ((value >>> 8) & 0xff));
            target.put(offset + 1, (byte) ((value) & 0xff));
        } else if (len <= 0) {
        } else if (len == 1) {
            target.put(offset, (byte) ((value >>> 8) & 0xff));
        } else {
            target.put(offset, (byte) ((value >>> 8) & 0xff));
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
    public void copyTo(CharValue target) {
        target.value = this.value;
    }

    @Override
    public CharValue copy() {
        return new CharValue(this.value);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source, 2);
    }

}
