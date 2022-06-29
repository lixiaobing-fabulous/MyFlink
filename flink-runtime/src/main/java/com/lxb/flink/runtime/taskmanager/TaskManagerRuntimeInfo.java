package com.lxb.flink.runtime.taskmanager;

import com.lxb.flink.configuration.Configuration;
import com.lxb.flink.configuration.TaskManagerOptions;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-29
 */
public interface TaskManagerRuntimeInfo {
    /**
     * Gets the configuration that the TaskManager was started with.
     *
     * @return The configuration that the TaskManager was started with.
     */
    Configuration getConfiguration();

    /**
     * Gets the list of temporary file directories.
     *
     * @return The list of temporary file directories.
     */
    String[] getTmpDirectories();

    /**
     * Checks whether the TaskManager should exit the JVM when the task thread throws
     * an OutOfMemoryError.
     *
     * @return True to terminate the JVM on an OutOfMemoryError, false otherwise.
     */
    boolean shouldExitJvmOnOutOfMemoryError();

    /**
     * Gets the external address of the TaskManager.
     *
     * @return The external address of the TaskManager.
     */
    String getTaskManagerExternalAddress();

    /**
     * Gets the bind address of the Taskmanager.
     *
     * @return The bind address of the TaskManager.
     */
    default String getTaskManagerBindAddress() {
        return getConfiguration().getString(TaskManagerOptions.BIND_HOST);
    }
}
