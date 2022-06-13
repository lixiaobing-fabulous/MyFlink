package com.lxb.flink.api.transformations;

import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.java.functions.KeySelector;
import com.lxb.flink.api.java.typeutils.TypeExtractor;
import com.lxb.flink.api.operators.ChainingStrategy;
import com.lxb.flink.api.operators.SimpleOperatorFactory;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.operators.StreamSink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SinkTransformation<T> extends PhysicalTransformation<Object> {
    private final Transformation<T> input;

    private final StreamOperatorFactory<Object> operatorFactory;
    private       KeySelector<T, ?>             stateKeySelector;

    private TypeInformation<?> stateKeyType;

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

    public StreamSink<T> getOperator() {
        return (StreamSink<T>) ((SimpleOperatorFactory) operatorFactory).getOperator();
    }

    public void setStateKeySelector(KeySelector<T, ?> stateKeySelector) {
        this.stateKeySelector = stateKeySelector;
    }

    public KeySelector<T, ?> getStateKeySelector() {
        return stateKeySelector;
    }

    public void setStateKeyType(TypeInformation<?> stateKeyType) {
        this.stateKeyType = stateKeyType;
    }

    public TypeInformation<?> getStateKeyType() {
        return stateKeyType;
    }

    @Override
    public Collection<Transformation<?>> getTransitivePredecessors() {
        List<Transformation<?>> result = new ArrayList<>();
        result.add(this);
        result.addAll(input.getTransitivePredecessors());
        return result;
    }


    @Override
    public final void setChainingStrategy(ChainingStrategy strategy) {
        operatorFactory.setChainingStrategy(strategy);
    }
}
