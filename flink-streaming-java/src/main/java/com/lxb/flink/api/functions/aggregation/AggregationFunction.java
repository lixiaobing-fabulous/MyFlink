package com.lxb.flink.api.functions.aggregation;

import com.lxb.flink.api.common.functions.ReduceFunction;

public abstract class AggregationFunction<T> implements ReduceFunction<T> {

    public enum AggregationType {
        SUM, MIN, MAX, MINBY, MAXBY,
    }

}
