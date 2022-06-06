package com.lxb.flink.api.watermark;

import com.lxb.flink.runtime.streamrecord.StreamElement;

public final class Watermark extends StreamElement {
    public static final Watermark MAX_WATERMARK = new Watermark(Long.MAX_VALUE);
    private final       long      timestamp;

    public Watermark(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                o != null && o.getClass() == Watermark.class && ((Watermark) o).timestamp == this.timestamp;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }

    @Override
    public String toString() {
        return "Watermark @ " + timestamp;
    }

}
