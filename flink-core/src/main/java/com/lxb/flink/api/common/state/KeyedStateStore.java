package com.lxb.flink.api.common.state;

public interface KeyedStateStore {

    <T> ValueState<T> getState(ValueStateDescriptor<T> stateProperties);

}
