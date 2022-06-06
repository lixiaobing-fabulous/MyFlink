package com.lxb.flink.runtime.streamrecord;

public final class StreamRecord<T> {
    private T       value;
    private long    timestamp;
    private boolean hasTimestamp;

    public StreamRecord(T value) {
        this.value = value;
    }

    public StreamRecord(T value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
        this.hasTimestamp = true;
    }

    public long getTimestamp() {
        if (hasTimestamp) {
            return timestamp;
        } else {
            return Long.MIN_VALUE;
        }
    }

    public T getValue() {
        return value;
    }

}
