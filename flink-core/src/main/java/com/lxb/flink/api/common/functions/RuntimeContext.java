package com.lxb.flink.api.common.functions;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.accumulators.Accumulator;
import com.lxb.flink.api.common.accumulators.DoubleCounter;
import com.lxb.flink.api.common.accumulators.IntCounter;
import com.lxb.flink.api.common.accumulators.LongCounter;
import com.lxb.flink.api.common.state.ValueState;
import com.lxb.flink.api.common.state.ValueStateDescriptor;

import java.io.Serializable;
import java.util.Map;

public interface RuntimeContext {

    String getTaskName();

    int getNumberOfParallelSubtasks();

    int getMaxNumberOfParallelSubtasks();

    int getIndexOfThisSubtask();

    int getAttemptNumber();

    String getTaskNameWithSubtasks();

    ExecutionConfig getExecutionConfig();

    ClassLoader getUserCodeClassLoader();

    <V, A extends Serializable> void addAccumulator(String name, Accumulator<V, A> accumulator);

    <V, A extends Serializable> Accumulator<V, A> getAccumulator(String name);

    Map<String, Accumulator<?, ?>> getAllAccumulators();

    IntCounter getIntCounter(String name);

    LongCounter getLongCounter(String name);

    DoubleCounter getDoubleCounter(String name);


    <T> ValueState<T> getState(ValueStateDescriptor<T> stateProperties);

}
