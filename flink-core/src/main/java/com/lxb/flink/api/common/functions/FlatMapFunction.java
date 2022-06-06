package com.lxb.flink.api.common.functions;

import com.lxb.flink.utl.Collector;

import java.io.Serializable;

public interface FlatMapFunction<T, O> extends Function, Serializable {
    void flatMap(T value, Collector<O> out) throws Exception;
}
