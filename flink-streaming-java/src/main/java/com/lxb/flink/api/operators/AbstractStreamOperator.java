package com.lxb.flink.api.operators;

public abstract class AbstractStreamOperator<OUT> implements StreamOperator<OUT> {
    protected ChainingStrategy chainingStrategy = ChainingStrategy.HEAD;

    @Override
    public void open() throws Exception {}

    @Override
    public void close() throws Exception {}

    @Override
    public void dispose() {

    }

    @Override
    public void setCurrentKey(Object key) {

    }

    @Override
    public Object getCurrentKey() {
        return null;
    }
}
