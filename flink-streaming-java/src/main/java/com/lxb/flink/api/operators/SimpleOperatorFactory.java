package com.lxb.flink.api.operators;

public class SimpleOperatorFactory<OUT> extends AbstractStreamOperatorFactory<OUT> {

    public static <OUT> SimpleOperatorFactory<OUT> of(StreamOperator<OUT> operator) {
        return null;
    }

    @Override
    public void setChainingStrategy(ChainingStrategy strategy) {

    }
}
