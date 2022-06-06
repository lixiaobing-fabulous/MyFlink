package com.lxb.flink.api.transformations;

import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.java.typeutils.TypeExtractor;
import com.lxb.flink.api.operators.ChainingStrategy;
import com.lxb.flink.api.operators.SimpleOperatorFactory;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.operators.StreamSink;

public class SinkTransformation<T> extends PhysicalTransformation<Object> {
    private Transformation<T> input;

    private StreamOperatorFactory<Object> operatorFactory;
    public SinkTransformation(
            Transformation<T> input,
            String name,
            StreamSink<T> operator,
            int parallelism) {
        this(input, name, SimpleOperatorFactory.of(operator), parallelism);
    }

    public SinkTransformation(
            Transformation<T> input,
            String name,
            StreamOperatorFactory<Object> operatorFactory,
            int parallelism) {
        super(name, TypeExtractor.getForClass(Object.class), parallelism);
        this.input = input;
        this.operatorFactory = operatorFactory;
    }

    public StreamOperatorFactory<Object> getOperatorFactory() {
        return operatorFactory;
    }

    public Transformation<T> getInput() {
        return input;
    }

    @Override
    public final void setChainingStrategy(ChainingStrategy strategy) {
        operatorFactory.setChainingStrategy(strategy);
    }
}
