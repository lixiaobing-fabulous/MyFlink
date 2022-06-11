package com.lxb.flink.api.common;

import static com.lxb.flink.utl.Preconditions.checkArgument;
import static com.lxb.flink.utl.Preconditions.checkNotNull;

public class TaskInfo {

    private final String taskName;
    private final String taskNameWithSubtasks;
    private final String allocationIDAsString;
    private final int    maxNumberOfParallelSubtasks;
    private final int    indexOfSubtask;
    private final int    numberOfParallelSubtasks;
    private final int    attemptNumber;

    public TaskInfo(
            String taskName,
            int maxNumberOfParallelSubtasks,
            int indexOfSubtask,
            int numberOfParallelSubtasks,
            int attemptNumber) {
        this(
                taskName,
                maxNumberOfParallelSubtasks,
                indexOfSubtask,
                numberOfParallelSubtasks,
                attemptNumber,
                "UNKNOWN");
    }

    public TaskInfo(
            String taskName,
            int maxNumberOfParallelSubtasks,
            int indexOfSubtask,
            int numberOfParallelSubtasks,
            int attemptNumber,
            String allocationIDAsString) {

        checkArgument(indexOfSubtask >= 0, "Task index must be a non-negative number.");
        checkArgument(maxNumberOfParallelSubtasks >= 1, "Max parallelism must be a positive number.");
        checkArgument(maxNumberOfParallelSubtasks >= numberOfParallelSubtasks, "Max parallelism must be >= than parallelism.");
        checkArgument(numberOfParallelSubtasks >= 1, "Parallelism must be a positive number.");
        checkArgument(indexOfSubtask < numberOfParallelSubtasks, "Task index must be less than parallelism.");
        checkArgument(attemptNumber >= 0, "Attempt number must be a non-negative number.");
        this.taskName = checkNotNull(taskName, "Task Name must not be null.");
        this.maxNumberOfParallelSubtasks = maxNumberOfParallelSubtasks;
        this.indexOfSubtask = indexOfSubtask;
        this.numberOfParallelSubtasks = numberOfParallelSubtasks;
        this.attemptNumber = attemptNumber;
        this.taskNameWithSubtasks = taskName + " (" + (indexOfSubtask + 1) + '/' + numberOfParallelSubtasks + ')';
        this.allocationIDAsString = checkNotNull(allocationIDAsString);
    }

    public String getTaskName() {
        return this.taskName;
    }

    public int getMaxNumberOfParallelSubtasks() {
        return maxNumberOfParallelSubtasks;
    }

    public int getIndexOfThisSubtask() {
        return this.indexOfSubtask;
    }

    public int getNumberOfParallelSubtasks() {
        return this.numberOfParallelSubtasks;
    }

    public int getAttemptNumber() {
        return this.attemptNumber;
    }

    public String getTaskNameWithSubtasks() {
        return this.taskNameWithSubtasks;
    }

    public String getAllocationIDAsString() {
        return allocationIDAsString;
    }
}
