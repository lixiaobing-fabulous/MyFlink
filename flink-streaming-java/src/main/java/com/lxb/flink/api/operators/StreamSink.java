package com.lxb.flink.api.operators;

import com.lxb.flink.functions.sink.SinkFunction;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

public class StreamSink<IN> extends AbstractUdfStreamOperator<Object, SinkFunction<IN>>
        implements OneInputStreamOperator<IN, Object> {
    public StreamSink(SinkFunction<IN> userFunction) {
        super(userFunction);
    }

    @Override
    public void processElement(StreamRecord<IN> element) throws Exception {

    }
}
