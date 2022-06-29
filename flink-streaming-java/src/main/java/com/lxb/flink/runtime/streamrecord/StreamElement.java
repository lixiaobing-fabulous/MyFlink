package com.lxb.flink.runtime.streamrecord;

import com.lxb.flink.api.watermark.Watermark;
import com.lxb.flink.runtime.streamstatus.StreamStatus;

public abstract class StreamElement {

    /**
     * Checks whether this element is a watermark.
     * @return True, if this element is a watermark, false otherwise.
     */
    public final boolean isWatermark() {
        return getClass() == Watermark.class;
    }

    /**
     * Checks whether this element is a stream status.
     * @return True, if this element is a stream status, false otherwise.
     */
    public final boolean isStreamStatus() {
        return getClass() == StreamStatus.class;
    }

    /**
     * Checks whether this element is a record.
     * @return True, if this element is a record, false otherwise.
     */
    public final boolean isRecord() {
        return getClass() == StreamRecord.class;
    }

    /**
     * Checks whether this element is a latency marker.
     * @return True, if this element is a latency marker, false otherwise.
     */
    public final boolean isLatencyMarker() {
        return getClass() == LatencyMarker.class;
    }

    /**
     * Casts this element into a StreamRecord.
     * @return This element as a stream record.
     * @throws java.lang.ClassCastException Thrown, if this element is actually not a stream record.
     */
    @SuppressWarnings("unchecked")
    public final <E> StreamRecord<E> asRecord() {
        return (StreamRecord<E>) this;
    }

    /**
     * Casts this element into a Watermark.
     * @return This element as a Watermark.
     * @throws java.lang.ClassCastException Thrown, if this element is actually not a Watermark.
     */
    public final Watermark asWatermark() {
        return (Watermark) this;
    }

    /**
     * Casts this element into a StreamStatus.
     * @return This element as a StreamStatus.
     * @throws java.lang.ClassCastException Thrown, if this element is actually not a Stream Status.
     */
    public final StreamStatus asStreamStatus() {
        return (StreamStatus) this;
    }

    /**
     * Casts this element into a LatencyMarker.
     * @return This element as a LatencyMarker.
     * @throws java.lang.ClassCastException Thrown, if this element is actually not a LatencyMarker.
     */
    public final LatencyMarker asLatencyMarker() {
        return (LatencyMarker) this;
    }
}
