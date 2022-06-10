package com.lxb.flink.api.common.accumulators;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-08
 */
public interface Accumulator<V, R> {
    void add(V value);

    R getLocalValue();

    void resetLocal();

    void merge(Accumulator<V, R> other);

    Accumulator<V, R> clone();

}
