package com.lxb.flink.api.transformations;

import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.java.functions.KeySelector;
import com.lxb.flink.api.operators.ChainingStrategy;
import com.lxb.flink.api.operators.OneInputStreamOperator;
import com.lxb.flink.api.operators.SimpleOperatorFactory;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.common.typeinfo.TypeInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OneInputTransformation<IN, OUT> extends PhysicalTransformation<OUT> {
    private final Transformation<IN> input;

    private final StreamOperatorFactory<OUT> operatorFactory;

    private KeySelector<IN, ?> stateKeySelector;

    private TypeInformation<?> stateKeyType;


    public OneInputTransformation(
            Transformation<IN> input,
            String name,
            OneInputStreamOperator<IN, OUT> operator,
            TypeInformation<OUT> outputType,
            int parallelism) {
        this(input, name, SimpleOperatorFactory.of(operator), outputType, parallelism);
    }

    public OneInputTransformation(
            Transformation<IN> input,
            String name,
            StreamOperatorFactory<OUT> operatorFactory,
            TypeInformation<OUT> outputType,
            int parallelism) {
        super(name, outputType, parallelism);
        this.input = input;
        this.operatorFactory = operatorFactory;
    }

    public Transformation<IN> getInput() {
        return input;
    }

    public TypeInformation<IN> getInputType() {
        return input.getOutputType();
    }

    public OneInputStreamOperator<IN, OUT> getOperator() {
        return (OneInputStreamOperator<IN, OUT>) ((SimpleOperatorFactory) operatorFactory).getOperator();
    }

    public StreamOperatorFactory<OUT> getOperatorFactory() {
        return operatorFactory;
    }

    public void setStateKeySelector(KeySelector<IN, ?> stateKeySelector) {
        this.stateKeySelector = stateKeySelector;
    }

    public KeySelector<IN, ?> getStateKeySelector() {
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
