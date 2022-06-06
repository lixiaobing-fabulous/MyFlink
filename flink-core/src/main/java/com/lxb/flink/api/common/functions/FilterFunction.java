package com.lxb.flink.api.common.functions;

import java.io.Serializable;

public interface FilterFunction<T> extends Function, Serializable {

    boolean filter(T value) throws Exception;
}
