package com.lxb.flink.api.operators;

import com.lxb.flink.runtime.state.OperatorStateBackend;

public interface StreamOperatorStateContext {
    boolean isRestored();

    OperatorStateBackend operatorStateBackend();

    AbstractKeyedStateBackend<?> keyedStateBackend();

    InternalTimeServiceManager<?> internalTimerServiceManager();

    CloseableIterable<StatePartitionStreamProvider> rawOperatorStateInputs();

}
