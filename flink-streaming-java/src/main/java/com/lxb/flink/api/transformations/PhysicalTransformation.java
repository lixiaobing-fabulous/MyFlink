package com.lxb.flink.api.transformations;

import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.operators.ChainingStrategy;
import com.lxb.flink.api.common.typeinfo.TypeInformation;

public abstract class PhysicalTransformation<T> extends Transformation<T> {

    PhysicalTransformation(
            String name,
            TypeInformation<T> outputType,
            int parallelism) {
        super(name, outputType, parallelism);
    }


    public abstract void setChainingStrategy(ChainingStrategy strategy);

}
