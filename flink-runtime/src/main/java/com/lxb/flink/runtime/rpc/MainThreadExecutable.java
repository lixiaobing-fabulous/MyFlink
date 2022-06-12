package com.lxb.flink.runtime.rpc;

import com.lxb.flink.api.common.time.Time;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface MainThreadExecutable {
    void runAsync(Runnable runnable);

    <V> CompletableFuture<V> callAsync(Callable<V> callable, Time callTimeout);

    void scheduleRunAsync(Runnable runnable, long delay);

}
