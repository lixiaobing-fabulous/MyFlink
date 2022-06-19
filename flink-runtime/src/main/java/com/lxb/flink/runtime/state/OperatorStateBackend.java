package com.lxb.flink.runtime.state;

import com.lxb.flink.api.common.state.OperatorStateStore;
import com.lxb.flink.utl.Disposable;

import java.io.Closeable;

public interface OperatorStateBackend extends
        OperatorStateStore,
        Closeable,
        Disposable {
    @Override
    void dispose();

}
