package com.lxb.flink.api.common.accumulators;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public class DoubleCounter implements SimpleAccumulator<Double> {

    private double localValue = 0;

    public DoubleCounter() {
    }

    public DoubleCounter(double value) {
        this.localValue = value;
    }

    @Override
    public void add(Double value) {
        localValue += value;
    }

    @Override
    public Double getLocalValue() {
        return localValue;
    }

    @Override
    public void merge(Accumulator<Double, Double> other) {
        this.localValue += other.getLocalValue();
    }

    @Override
    public void resetLocal() {
        this.localValue = 0;
    }

    @Override
    public DoubleCounter clone() {
        DoubleCounter result = new DoubleCounter();
        result.localValue = localValue;
        return result;
    }


    public void add(double value) {
        localValue += value;
    }

    public double getLocalValuePrimitive() {
        return this.localValue;
    }


    @Override
    public String toString() {
        return "DoubleCounter " + this.localValue;
    }
}
