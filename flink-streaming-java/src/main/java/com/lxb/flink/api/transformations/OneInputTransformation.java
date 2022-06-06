package com.lxb.flink.api.transformations;

import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.java.functions.KeySelector;
import com.lxb.flink.api.operators.ChainingStrategy;
import com.lxb.flink.api.operators.OneInputStreamOperator;
import com.lxb.flink.api.operators.SimpleOperatorFactory;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.common.typeinfo.TypeInformation;

public class OneInputTransformation<IN, OUT> extends PhysicalTransformation<OUT> {
    private final Transformation<IN> input;

    private final StreamOperatorFactory<OUT> operatorFactory;
    private       KeySelector<IN, ?>         stateKeySelector;
    private       TypeInformation<?>         stateKeyType;


    public OneInputTransformation(
            Transformation<IN> input,
            String name,
            OneInputStreamOperator<IN, OUT> operator,
            TypeInformation<OUT> outputType,
            int parallelism) {
        this(input,
                name,
                SimpleOperatorFactory.of(operator),
                outputType,
                parallelism);
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

    @Override
    public final void setChainingStrategy(ChainingStrategy strategy) {
        operatorFactory.setChainingStrategy(strategy);
    }

    public Transformation<IN> getInput() {
        return input;
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

    public TypeInformation<IN> getInputType() {
        return input.getOutputType();
    }

    /**
     * Returns the {@code StreamOperatorFactory} of this Transformation.
     */
    public StreamOperatorFactory<OUT> getOperatorFactory() {
        return operatorFactory;
    }

}
