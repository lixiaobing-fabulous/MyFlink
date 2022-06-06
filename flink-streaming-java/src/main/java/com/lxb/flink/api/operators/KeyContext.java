package com.lxb.flink.api.operators;

public interface KeyContext {

    void setCurrentKey(Object key);
    Object getCurrentKey();
}
