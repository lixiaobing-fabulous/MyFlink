package com.lxb.flink.api.common.accumulators;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public class LongCounter implements SimpleAccumulator<Long> {
    private long localValue;

    public LongCounter() {
    }

    public LongCounter(long value) {
        this.localValue = value;
    }

    @Override
    public void add(Long value) {
        this.localValue += value;
    }

    @Override
    public Long getLocalValue() {
        return this.localValue;
    }

    @Override
    public void resetLocal() {
        this.localValue = 0;
    }

    @Override
    public void merge(Accumulator<Long, Long> other) {
        this.localValue += other.getLocalValue();
    }

    @Override
    public Accumulator<Long, Long> clone() {
        LongCounter result = new LongCounter();
        result.localValue = localValue;
        return result;
    }

    public void add(long value) {
        this.localValue += value;
    }

    public long getLocalValuePrimitive() {
        return this.localValue;
    }


    @Override
    public String toString() {
        return "LongCounter " + this.localValue;
    }

}
