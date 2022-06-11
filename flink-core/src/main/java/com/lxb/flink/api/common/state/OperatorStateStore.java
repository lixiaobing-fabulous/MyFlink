package com.lxb.flink.api.common.state;

import java.util.Set;

public interface OperatorStateStore {
    Set<String> getRegisteredStateNames();

    Set<String> getRegisteredBroadcastStateNames();

}
