package com.lxb.flink.api.operators;

import com.lxb.flink.api.watermark.Watermark;
import com.lxb.flink.utl.Collector;

public interface Output<T> extends Collector<T> {

    void emitWatermark(Watermark mark);
}
