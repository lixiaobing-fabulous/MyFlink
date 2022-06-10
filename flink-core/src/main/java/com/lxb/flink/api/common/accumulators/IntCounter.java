package com.lxb.flink.api.common.accumulators;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public class IntCounter implements SimpleAccumulator<Integer> {
    private int localValue = 0;

    public IntCounter() {
    }

    public IntCounter(int value) {
        this.localValue = value;
    }

    @Override
    public void add(Integer value) {
        localValue += value;
    }

    @Override
    public Integer getLocalValue() {
        return localValue;
    }

    @Override
    public void resetLocal() {
        this.localValue = 0;
    }

    @Override
    public void merge(Accumulator<Integer, Integer> other) {
        this.localValue += other.getLocalValue();
    }

    @Override
    public Accumulator<Integer, Integer> clone() {
        IntCounter result = new IntCounter();
        result.localValue = localValue;
        return result;
    }

    public void add(int value) {
        localValue += value;
    }

    public int getLocalValuePrimitive() {
        return this.localValue;
    }

    @Override
    public String toString() {
        return "IntCounter " + this.localValue;
    }

}
