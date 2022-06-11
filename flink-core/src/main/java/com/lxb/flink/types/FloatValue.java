package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public class FloatValue implements Comparable<FloatValue>, ResettableValue<FloatValue>, CopyableValue<FloatValue>, Key<FloatValue> {

    private float value;

    public FloatValue() {
        this.value = 0;
    }

    public FloatValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void setValue(FloatValue value) {
        this.value = value.value;
    }

    @Override
    public void read(DataInputView in) throws IOException {
        this.value = in.readFloat();
    }

    @Override
    public void write(DataOutputView out) throws IOException {
        out.writeFloat(this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public int compareTo(FloatValue o) {
        final double other = o.value;
        return this.value < other ? -1 : this.value > other ? 1 : 0;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FloatValue) {
            final FloatValue other = (FloatValue) obj;
            return Float.floatToIntBits(this.value) == Float.floatToIntBits(other.value);
        }
        return false;
    }

    @Override
    public int getBinaryLength() {
        return 4;
    }

    @Override
    public void copyTo(FloatValue target) {
        target.value = this.value;
    }

    @Override
    public FloatValue copy() {
        return new FloatValue(this.value);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source, 4);
    }

}
