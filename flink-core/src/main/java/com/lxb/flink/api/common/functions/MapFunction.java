package com.lxb.flink.api.common.functions;

import java.io.Serializable;

public interface MapFunction<T, O> extends Function, Serializable {
    O map(T value);
}
