package com.lxb.flink.types;

public interface ResettableValue<T extends Value> extends Value {
    void setValue(T value);

}
