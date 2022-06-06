package com.lxb.flink.api.datastream;

import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.environment.StreamExecutionEnvironment;

public class SingleOutputStreamOperator<T> extends DataStream<T> {

    public SingleOutputStreamOperator(StreamExecutionEnvironment environment, Transformation<T> transformation) {
        super(environment, transformation);
    }

    public SingleOutputStreamOperator<T> setParallelism(int parallelism) {
        return this;
    }

}
