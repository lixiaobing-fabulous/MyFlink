package com.lxb.flink.runtime.tasks;

public interface ProcessingTimeCallback {
    void onProcessingTime(long timestamp) throws Exception;

}
