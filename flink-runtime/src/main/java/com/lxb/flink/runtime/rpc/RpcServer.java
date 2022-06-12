package com.lxb.flink.runtime.rpc;

import java.util.concurrent.CompletableFuture;

public interface RpcServer extends StartStoppable, MainThreadExecutable, RpcGateway {
    CompletableFuture<Void> getTerminationFuture();

}
