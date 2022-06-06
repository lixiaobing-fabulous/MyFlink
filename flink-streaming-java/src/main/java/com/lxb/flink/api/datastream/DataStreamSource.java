package com.lxb.flink.api.datastream;

import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.environment.StreamExecutionEnvironment;

public class DataStreamSource<T> extends SingleOutputStreamOperator<T> {
    public DataStreamSource(StreamExecutionEnvironment environment, Transformation<T> transformation) {
        super(environment, transformation);
    }
}
