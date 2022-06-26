package com.lxb.flink.runtime.state;

import com.lxb.flink.api.common.state.State;

public interface KeyedStateFunction<K, S extends State> {
    void process(K key, S state) throws Exception;

}
