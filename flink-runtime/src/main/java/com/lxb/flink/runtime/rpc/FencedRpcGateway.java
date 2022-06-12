package com.lxb.flink.runtime.rpc;

import java.io.Serializable;

public interface FencedRpcGateway<F extends Serializable> extends RpcGateway {
    F getFencingToken();
}
