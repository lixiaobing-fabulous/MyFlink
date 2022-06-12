package com.lxb.flink.runtime.rpc;

import com.lxb.flink.runtime.concurrent.ScheduledExecutor;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface RpcService {
    String getAddress();

    int getPort();

    <C extends RpcGateway> CompletableFuture<C> connect(
            String address,
            Class<C> clazz);

    <F extends Serializable, C extends FencedRpcGateway<F>> CompletableFuture<C> connect(
            String address,
            F fencingToken,
            Class<C> clazz);

    <C extends RpcEndpoint & RpcGateway> RpcServer startServer(C rpcEndpoint);

    <F extends Serializable> RpcServer fenceRpcServer(RpcServer rpcServer, F fencingToken);

    void stopServer(RpcServer selfGateway);

    CompletableFuture<Void> stopService();

    CompletableFuture<Void> getTerminationFuture();

    Executor getExecutor();

    ScheduledExecutor getScheduledExecutor();

    ScheduledFuture<?> scheduleRunnable(Runnable runnable, long delay, TimeUnit unit);

    void execute(Runnable runnable);

    <T> CompletableFuture<T> execute(Callable<T> callable);

}
