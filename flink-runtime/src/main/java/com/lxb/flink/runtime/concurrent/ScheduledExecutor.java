package com.lxb.flink.runtime.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface ScheduledExecutor extends Executor {
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    ScheduledFuture<?> scheduleAtFixedRate(
            Runnable command,
            long initialDelay,
            long period,
            TimeUnit unit);

    ScheduledFuture<?> scheduleWithFixedDelay(
            Runnable command,
            long initialDelay,
            long delay,
            TimeUnit unit);

}
