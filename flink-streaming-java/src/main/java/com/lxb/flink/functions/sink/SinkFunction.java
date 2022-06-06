package com.lxb.flink.functions.sink;

import com.lxb.flink.api.common.functions.Function;

import java.io.Serializable;

public interface SinkFunction<IN> extends Function, Serializable {
}
