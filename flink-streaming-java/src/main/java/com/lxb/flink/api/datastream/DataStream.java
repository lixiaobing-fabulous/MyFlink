package com.lxb.flink.api.datastream;

import com.lxb.flink.api.common.functions.FilterFunction;
import com.lxb.flink.api.common.functions.FlatMapFunction;
import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.environment.StreamExecutionEnvironment;
import com.lxb.flink.api.java.functions.KeySelector;
import com.lxb.flink.api.operators.OneInputStreamOperator;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.operators.SimpleOperatorFactory;
import com.lxb.flink.api.operators.StreamFlatMap;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.transformations.OneInputTransformation;

public class DataStream<T> {

    protected final StreamExecutionEnvironment environment;
    protected final Transformation<T>          transformation;

    public DataStream(StreamExecutionEnvironment environment, Transformation<T> transformation) {
        this.environment = environment;
        this.transformation = transformation;
    }

    public int getId() {
        return transformation.getId();
    }

    public int getParallelism() {
        return transformation.getParallelism();
    }


    public <R> SingleOutputStreamOperator<R> flatMap(FlatMapFunction<T, R> flatMapper) {

        return null;
    }


    public SingleOutputStreamOperator<T> filter(FilterFunction<T> filter) {
        return null;
    }

    public <K> KeyedStream<T, K> keyBy(KeySelector<T, K> key) {
        return null;
    }


    public DataStreamSink<T> print() {
        return null;
    }

    public <R> SingleOutputStreamOperator<R> flatMap(FlatMapFunction<T, R> flatMapper, TypeInformation<R> outputType) {
        return transform("Flat Map", outputType, new StreamFlatMap<>(clean(flatMapper)));
    }

    public <R> SingleOutputStreamOperator<R> transform(String operatorName, TypeInformation<R> outTypeInfo,
                                                       OneInputStreamOperator<T, R> operator) {
        return doTransform(operatorName, outTypeInfo, SimpleOperatorFactory.of(operator));
    }

    protected <R> SingleOutputStreamOperator<R> doTransform(
            String operatorName,
            TypeInformation<R> outTypeInfo,
            StreamOperatorFactory<R> operatorFactory) {

        // read the output type of the input Transform to coax out errors about MissingTypeInfo
        transformation.getOutputType();

        OneInputTransformation<T, R> resultTransform = new OneInputTransformation<>(
                this.transformation,
                operatorName,
                operatorFactory,
                outTypeInfo,
                environment.getParallelism());

        @SuppressWarnings({"unchecked", "rawtypes"})
        SingleOutputStreamOperator<R> returnStream = new SingleOutputStreamOperator(environment, resultTransform);

        getExecutionEnvironment().addOperator(resultTransform);

        return returnStream;
    }


    protected <F> F clean(F f) {
        return getExecutionEnvironment().clean(f);
    }

    public StreamExecutionEnvironment getExecutionEnvironment() {
        return environment;
    }


}
