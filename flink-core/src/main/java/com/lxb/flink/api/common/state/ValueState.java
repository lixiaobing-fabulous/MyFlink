package com.lxb.flink.api.common.state;

import java.io.IOException;

public interface ValueState<T> extends State {

    T value() throws IOException;

    void update(T value) throws IOException;
}
