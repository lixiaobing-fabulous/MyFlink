package com.lxb.flink.api.operators;

import com.lxb.flink.runtime.streamrecord.StreamRecord;

public interface OneInputStreamOperator<IN, OUT> extends StreamOperator<OUT> {

    void processElement(StreamRecord<IN> element) throws Exception;

}
