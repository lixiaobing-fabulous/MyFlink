package com.lxb.flink.api.operators;

import com.lxb.flink.api.watermark.Watermark;

public final class TimestampedCollector<T> implements Output<T> {

    @Override
    public void collect(T record) {

    }

    @Override
    public void close() {

    }

    @Override
    public void emitWatermark(Watermark mark) {

    }
}
