package com.lxb.flink.api.common.functions;

import java.io.Serializable;

public interface ReduceFunction<T> extends Function, Serializable {

    T reduce(T value1, T value2) throws Exception;
}
