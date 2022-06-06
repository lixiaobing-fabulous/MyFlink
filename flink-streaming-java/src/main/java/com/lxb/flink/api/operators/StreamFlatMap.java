package com.lxb.flink.api.operators;

import com.lxb.flink.api.common.functions.FlatMapFunction;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

public class StreamFlatMap<IN, OUT> extends AbstractUdfStreamOperator<OUT, FlatMapFunction<IN, OUT>> implements OneInputStreamOperator<IN, OUT> {
    private transient TimestampedCollector<OUT> collector;

    public StreamFlatMap(FlatMapFunction<IN, OUT> userFunction) {
        super(userFunction);
    }

    @Override
    public void open() throws Exception {
        super.open();
        collector = new TimestampedCollector<>();
    }

    @Override
    public void processElement(StreamRecord<IN> element) throws Exception {
        userFunction.flatMap(element.getValue(), collector);

    }
}
