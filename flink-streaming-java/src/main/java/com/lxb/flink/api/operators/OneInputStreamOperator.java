package com.lxb.flink.api.operators;

import com.lxb.flink.api.watermark.Watermark;
import com.lxb.flink.runtime.streamrecord.LatencyMarker;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

public interface OneInputStreamOperator<IN, OUT> extends StreamOperator<OUT> {

    void processElement(StreamRecord<IN> element) throws Exception;

    void processWatermark(Watermark mark) throws Exception;

    void processLatencyMarker(LatencyMarker latencyMarker) throws Exception;


}
