package com.lxb.flink.runtime.tasks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

public interface ProcessingTimeService {
    long getCurrentProcessingTime();

    ScheduledFuture<?> registerTimer(long timestamp, ProcessingTimeCallback target);

    ScheduledFuture<?> scheduleAtFixedRate(ProcessingTimeCallback callback, long initialDelay, long period);

    ScheduledFuture<?> scheduleWithFixedDelay(ProcessingTimeCallback callback, long initialDelay, long period);

    CompletableFuture<Void> quiesce();

}
