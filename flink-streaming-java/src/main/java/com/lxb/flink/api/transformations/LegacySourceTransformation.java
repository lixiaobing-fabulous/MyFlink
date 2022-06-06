package com.lxb.flink.api.transformations;

import com.lxb.flink.api.operators.ChainingStrategy;
import com.lxb.flink.api.operators.SimpleOperatorFactory;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.operators.StreamSource;
import com.lxb.flink.api.common.typeinfo.TypeInformation;

public class LegacySourceTransformation<T> extends PhysicalTransformation<T> {
    private final StreamOperatorFactory<T> operatorFactory;
    public LegacySourceTransformation(
            String name,
            StreamSource<T, ?> operator,
            TypeInformation<T> outputType,
            int parallelism) {
        this(name, SimpleOperatorFactory.of(operator), outputType, parallelism);
    }

    public LegacySourceTransformation(
            String name,
            StreamOperatorFactory<T> operatorFactory,
            TypeInformation<T> outputType,
            int parallelism) {
        super(name, outputType, parallelism);
        this.operatorFactory = operatorFactory;
    }

    public StreamOperatorFactory<T> getOperatorFactory() {
        return operatorFactory;
    }

    @Override
    public final void setChainingStrategy(ChainingStrategy strategy) {
        operatorFactory.setChainingStrategy(strategy);
    }
}
