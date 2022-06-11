package com.lxb.flink.api.common.functions.util;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.TaskInfo;
import com.lxb.flink.api.common.accumulators.Accumulator;
import com.lxb.flink.api.common.accumulators.AccumulatorHelper;
import com.lxb.flink.api.common.accumulators.DoubleCounter;
import com.lxb.flink.api.common.accumulators.IntCounter;
import com.lxb.flink.api.common.accumulators.LongCounter;
import com.lxb.flink.api.common.functions.RuntimeContext;
import com.lxb.flink.api.common.state.ValueState;
import com.lxb.flink.api.common.state.ValueStateDescriptor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

public abstract class AbstractRuntimeUDFContext implements RuntimeContext {
    private final TaskInfo                       taskInfo;
    private final ClassLoader                    userCodeClassLoader;
    private final ExecutionConfig                executionConfig;
    private final Map<String, Accumulator<?, ?>> accumulators;

    public AbstractRuntimeUDFContext(TaskInfo taskInfo,
                                     ClassLoader userCodeClassLoader,
                                     ExecutionConfig executionConfig,
                                     Map<String, Accumulator<?, ?>> accumulators) {
        this.taskInfo = checkNotNull(taskInfo);
        this.userCodeClassLoader = userCodeClassLoader;
        this.executionConfig = executionConfig;
        this.accumulators = checkNotNull(accumulators);
    }

    @Override
    public ExecutionConfig getExecutionConfig() {
        return executionConfig;
    }

    @Override
    public String getTaskName() {
        return taskInfo.getTaskName();
    }

    @Override
    public int getNumberOfParallelSubtasks() {
        return taskInfo.getNumberOfParallelSubtasks();
    }

    @Override
    public int getMaxNumberOfParallelSubtasks() {
        return taskInfo.getMaxNumberOfParallelSubtasks();
    }

    @Override
    public int getIndexOfThisSubtask() {
        return taskInfo.getIndexOfThisSubtask();
    }

    @Override
    public int getAttemptNumber() {
        return taskInfo.getAttemptNumber();
    }

    @Override
    public String getTaskNameWithSubtasks() {
        return taskInfo.getTaskNameWithSubtasks();
    }

    @Override
    public IntCounter getIntCounter(String name) {
        return (IntCounter) getAccumulator(name, IntCounter.class);
    }

    @Override
    public LongCounter getLongCounter(String name) {
        return (LongCounter) getAccumulator(name, LongCounter.class);
    }

    @Override
    public DoubleCounter getDoubleCounter(String name) {
        return (DoubleCounter) getAccumulator(name, DoubleCounter.class);
    }

    @Override
    public <V, A extends Serializable> void addAccumulator(String name, Accumulator<V, A> accumulator) {
        if (accumulators.containsKey(name)) {
            throw new UnsupportedOperationException("The accumulator '" + name
                    + "' already exists and cannot be added.");
        }
        accumulators.put(name, accumulator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V, A extends Serializable> Accumulator<V, A> getAccumulator(String name) {
        return (Accumulator<V, A>) accumulators.get(name);
    }

    @Override
    public Map<String, Accumulator<?, ?>> getAllAccumulators() {
        return Collections.unmodifiableMap(this.accumulators);
    }

    @Override
    public ClassLoader getUserCodeClassLoader() {
        return this.userCodeClassLoader;
    }

    private <V, A extends Serializable> Accumulator<V, A> getAccumulator(String name,
                                                                         Class<? extends Accumulator<V, A>> accumulatorClass) {

        Accumulator<?, ?> accumulator = accumulators.get(name);

        if (accumulator != null) {
            AccumulatorHelper.compareAccumulatorTypes(name, accumulator.getClass(), accumulatorClass);
        } else {
            // Create new accumulator
            try {
                accumulator = accumulatorClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot create accumulator " + accumulatorClass.getName());
            }
            accumulators.put(name, accumulator);
        }
        return (Accumulator<V, A>) accumulator;
    }

    public <T> ValueState<T> getState(ValueStateDescriptor<T> stateProperties) {
        throw new UnsupportedOperationException(
                "This state is only accessible by functions executed on a KeyedStream");
    }

    public String getAllocationIDAsString() {
        return taskInfo.getAllocationIDAsString();
    }

}
