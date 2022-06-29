/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lxb.flink.runtime.execution;


import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.JobID;
import com.lxb.flink.api.common.TaskInfo;
import com.lxb.flink.configuration.Configuration;
import com.lxb.flink.runtime.executiongraph.ExecutionAttemptID;
import com.lxb.flink.runtime.jobgraph.JobVertexID;
import com.lxb.flink.runtime.jobgraph.tasks.InputSplitProvider;
import com.lxb.flink.runtime.taskmanager.TaskManagerRuntimeInfo;

/**
 * The Environment gives the code executed in a task access to the task's properties
 * (such as name, parallelism), the configurations, the data stream readers and writers,
 * as well as the various components that are provided by the TaskManager, such as
 * memory manager, I/O manager, ...
 */
public interface Environment {

	ExecutionConfig getExecutionConfig();

	JobID getJobID();

	JobVertexID getJobVertexId();

	/**
	 * Gets the ID of the task execution attempt.
	 *
	 * @return The ID of the task execution attempt.
	 */
	ExecutionAttemptID getExecutionId();

	/**
	 * Returns the task-wide configuration object, originally attached to the job vertex.
	 *
	 * @return The task-wide configuration
	 */
	Configuration getTaskConfiguration();

	/**
	 * Gets the task manager info, with configuration and hostname.
	 * 
	 * @return The task manager info, with configuration and hostname. 
	 */
	TaskManagerRuntimeInfo getTaskManagerInfo();

	/**
	 * Returns the job-wide configuration object that was attached to the JobGraph.
	 *
	 * @return The job-wide configuration
	 */
	Configuration getJobConfiguration();

	/**
	 * Returns the {@link TaskInfo} object associated with this subtask
	 *
	 * @return TaskInfo for this subtask
	 */
	TaskInfo getTaskInfo();

	/**
	 * Returns the input split provider assigned to this environment.
	 *
	 * @return The input split provider or {@code null} if no such
	 *         provider has been assigned to this environment.
	 */
	InputSplitProvider getInputSplitProvider();

//	/**
//	 * Gets the gateway through which operators can send events to the operator coordinators.
//	 */
//	TaskOperatorEventGateway getOperatorCoordinatorEventGateway();
//
//	/**
//	 * Returns the current {@link MemoryManager}.
//	 *
//	 * @return the current {@link MemoryManager}.
//	 */
//	MemoryManager getMemoryManager();
//
	/**
	 * Returns the user code class loader
	 */
	ClassLoader getUserClassLoader();
//
//	TaskStateManager getTaskStateManager();
//
//	/**
//	 * Return the registry for accumulators which are periodically sent to the job manager.
//	 * @return the registry
//	 */
//	AccumulatorRegistry getAccumulatorRegistry();
//
//	/**
//	 * Returns the registry for {@link InternalKvState} instances.
//	 *
//	 * @return KvState registry
//	 */
//	TaskKvStateRegistry getTaskKvStateRegistry();
//
//	/**
//	 * Marks task execution failed for an external reason (a reason other than the task code itself
//	 * throwing an exception). If the task is already in a terminal state
//	 * (such as FINISHED, CANCELED, FAILED), or if the task is already canceling this does nothing.
//	 * Otherwise it sets the state to FAILED, and, if the invokable code is running,
//	 * starts an asynchronous thread that aborts that code.
//	 *
//	 * <p>This method never blocks.
//	 */
//	void failExternally(Throwable cause);
//
//	// --------------------------------------------------------------------------------------------
//	//  Fields relevant to the I/O system. Should go into Task
//	// --------------------------------------------------------------------------------------------
//
//	ResultPartitionWriter getWriter(int index);
//
//	ResultPartitionWriter[] getAllWriters();
//
//	IndexedInputGate getInputGate(int index);
//
//	IndexedInputGate[] getAllInputGates();
//
//	TaskEventDispatcher getTaskEventDispatcher();
}
