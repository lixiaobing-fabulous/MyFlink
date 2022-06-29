package com.lxb.flink.runtime.jobgraph.tasks;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.configuration.Configuration;
import com.lxb.flink.runtime.execution.Environment;
import com.lxb.flink.runtime.jobgraph.OperatorID;
import com.lxb.flink.runtime.operators.coordination.OperatorEvent;
import com.lxb.flink.utl.FlinkException;
import com.lxb.flink.utl.SerializedValue;

public abstract class AbstractInvokable {
    /** The environment assigned to this invokable. */
    private final Environment environment;

    /** Flag whether cancellation should interrupt the executing thread. */
    private volatile boolean shouldInterruptOnCancel = true;

    /**
     * Create an Invokable task and set its environment.
     *
     * @param environment The environment assigned to this invokable.
     */
    public AbstractInvokable(Environment environment) {
        this.environment = checkNotNull(environment);
    }

    public abstract void invoke() throws Exception;

    public void cancel() throws Exception {
        // The default implementation does nothing.
    }

    public void setShouldInterruptOnCancel(boolean shouldInterruptOnCancel) {
        this.shouldInterruptOnCancel = shouldInterruptOnCancel;
    }

    /**
     * Checks whether the task should be interrupted during cancellation.
     * This method is check both for the initial interrupt, as well as for the
     * repeated interrupt. Setting the interruption to false via
     * {@link #setShouldInterruptOnCancel(boolean)} is a way to stop further interrupts
     * from happening.
     */
    public boolean shouldInterruptOnCancel() {
        return shouldInterruptOnCancel;
    }

    // ------------------------------------------------------------------------
    //  Access to Environment and Configuration
    // ------------------------------------------------------------------------

    /**
     * Returns the environment of this task.
     *
     * @return The environment of this task.
     */
    public final Environment getEnvironment() {
        return this.environment;
    }

    /**
     * Returns the user code class loader of this invokable.
     *
     * @return user code class loader of this invokable.
     */
    public final ClassLoader getUserCodeClassLoader() {
        return getEnvironment().getUserClassLoader();
    }

    /**
     * Returns the current number of subtasks the respective task is split into.
     *
     * @return the current number of subtasks the respective task is split into
     */
    public int getCurrentNumberOfSubtasks() {
        return this.environment.getTaskInfo().getNumberOfParallelSubtasks();
    }

    /**
     *
     */
    public final Configuration getTaskConfiguration() {
        return this.environment.getTaskConfiguration();
    }

    /**
     *
     */
    public Configuration getJobConfiguration() {
        return this.environment.getJobConfiguration();
    }

    /**
     * Returns the global ExecutionConfig.
     */
    public ExecutionConfig getExecutionConfig() {
        return this.environment.getExecutionConfig();
    }

    public void dispatchOperatorEvent(OperatorID operator, SerializedValue<OperatorEvent> event) throws FlinkException {
        throw new UnsupportedOperationException("dispatchOperatorEvent not supported by " + getClass().getName());
    }
}
