package com.lxb.flink.types;

import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

public class DoubleValue implements Comparable<DoubleValue>, ResettableValue<DoubleValue>, CopyableValue<DoubleValue>, Key<DoubleValue> {
    private double value;

    public DoubleValue() {
        this.value = 0;
    }

    public DoubleValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void setValue(DoubleValue value) {
        this.value = value.value;
    }

    @Override
    public void read(DataInputView in) throws IOException {
        this.value = in.readDouble();
    }

    @Override
    public void write(DataOutputView out) throws IOException {
        out.writeDouble(this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public int compareTo(DoubleValue o) {
        final double other = o.value;
        return this.value < other ? -1 : this.value > other ? 1 : 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(this.value);
        return 31 + (int) (temp ^ temp >>> 32);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DoubleValue) {
            final DoubleValue other = (DoubleValue) obj;
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
        }
        return false;
    }

    @Override
    public int getBinaryLength() {
        return 8;
    }

    @Override
    public void copyTo(DoubleValue target) {
        target.value = this.value;
    }

    @Override
    public DoubleValue copy() {
        return new DoubleValue(this.value);
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {
        target.write(source, 8);
    }

}
