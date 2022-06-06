package com.lxb.flink.api.datastream;

import com.lxb.flink.api.common.functions.ReduceFunction;
import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.environment.StreamExecutionEnvironment;

public class KeyedStream<T, KEY> extends DataStream<T> {

    public KeyedStream(StreamExecutionEnvironment environment, Transformation<T> transformation) {
        super(environment, transformation);
    }

    public SingleOutputStreamOperator<T> reduce(ReduceFunction<T> reducer) {
        return null;
    }

    public DataStreamSink<T> print() {
        return null;
    }


}
