package com.lxb.flink.api.java.functions;

import com.lxb.flink.api.common.functions.Function;

import java.io.Serializable;

public interface KeySelector<IN, KEY> extends Function, Serializable {

    KEY getKey(IN value) throws Exception;
}
