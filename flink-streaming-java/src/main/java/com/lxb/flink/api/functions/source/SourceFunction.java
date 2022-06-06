package com.lxb.flink.api.functions.source;

import com.lxb.flink.api.common.functions.Function;
import com.lxb.flink.api.watermark.Watermark;

import java.io.Serializable;

public interface SourceFunction<T> extends Function, Serializable {

    void run(SourceContext<T> ctx) throws Exception;

    void cancel();

    interface SourceContext<T> {

        void collect(T element);

        void collectWithTimestamp(T element, long timestamp);

        void emitWatermark(Watermark mark);

        void markAsTemporarilyIdle();

        Object getCheckpointLock();

        void close();

    }
}
