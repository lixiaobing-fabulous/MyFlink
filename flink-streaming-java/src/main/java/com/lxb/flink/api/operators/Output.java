package com.lxb.flink.api.operators;

import com.lxb.flink.api.watermark.Watermark;
import com.lxb.flink.runtime.streamrecord.LatencyMarker;
import com.lxb.flink.runtime.streamrecord.StreamRecord;
import com.lxb.flink.utl.Collector;
import com.lxb.flink.utl.OutputTag;

public interface Output<T> extends Collector<T> {

    void emitWatermark(Watermark mark);

    void emitLatencyMarker(LatencyMarker latencyMarker);

    <X> void collect(OutputTag<X> outputTag, StreamRecord<X> record);


}
