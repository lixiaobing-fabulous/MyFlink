package com.lxb.flink.api.operators;

public class StreamingRuntimeContext {
    public int getIndexOfThisSubtask() {
        return 0;
    }

    public int getNumberOfParallelSubtasks() {
        return 0;
    }
}
