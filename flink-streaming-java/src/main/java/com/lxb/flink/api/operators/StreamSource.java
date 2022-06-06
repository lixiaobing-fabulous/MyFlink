package com.lxb.flink.api.operators;

import com.lxb.flink.api.functions.source.SourceFunction;

public class StreamSource<OUT, SRC extends SourceFunction<OUT>> extends AbstractUdfStreamOperator<OUT, SRC> {
    public StreamSource(SRC sourceFunction) {
        super(sourceFunction);
        this.chainingStrategy = ChainingStrategy.HEAD;
    }
}
