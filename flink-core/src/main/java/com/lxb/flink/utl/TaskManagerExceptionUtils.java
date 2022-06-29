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

package com.lxb.flink.utl;


import static com.lxb.flink.utl.ExceptionUtils.tryEnrichOutOfMemoryError;

import javax.annotation.Nullable;

import com.lxb.flink.configuration.TaskManagerOptions;


/**
 * Exception utils to handle and enrich exceptions occurring in TaskManager.
 */
public class TaskManagerExceptionUtils {
	private static final String TM_DIRECT_OOM_ERROR_MESSAGE = String.format(
		"Direct buffer memory. The direct out-of-memory error has occurred. This can mean two things: either job(s) require(s) " +
			"a larger size of JVM direct memory or there is a direct memory leak. The direct memory can be " +
			"allocated by user code or some of its dependencies. In this case '%s' configuration option should be " +
			"increased. Flink framework and its dependencies also consume the direct memory, mostly for network " +
			"communication. The most of network memory is managed by Flink and should not result in out-of-memory " +
			"error. In certain special cases, in particular for jobs with high parallelism, the framework may " +
			"require more direct memory which is not managed by Flink. In this case '%s' configuration option " +
			"should be increased. If the error persists then there is probably a direct memory leak in user code or " +
			"some of its dependencies which has to be investigated and fixed. The task executor has to be shutdown...",
		TaskManagerOptions.TASK_OFF_HEAP_MEMORY.key(),
		TaskManagerOptions.FRAMEWORK_OFF_HEAP_MEMORY.key());

	private static final String TM_METASPACE_OOM_ERROR_MESSAGE = String.format(
		"Metaspace. The metaspace out-of-memory error has occurred. This can mean two things: either the job requires " +
			"a larger size of JVM metaspace to load classes or there is a class loading leak. In the first case " +
			"'%s' configuration option should be increased. If the error persists (usually in cluster after " +
			"several job (re-)submissions) then there is probably a class loading leak in user code or some of its dependencies " +
			"which has to be investigated and fixed. The task executor has to be shutdown...",
		TaskManagerOptions.JVM_METASPACE.key());

	private TaskManagerExceptionUtils() {
	}

	/**
	 * Tries to enrich the passed exception with additional information.
	 *
	 * <p>This method improves error message for direct and metaspace {@link OutOfMemoryError}.
	 * It adds description of possible causes and ways of resolution.
	 *
	 * @param exception exception to enrich if not {@code null}
	 * @return the enriched exception or the original if no additional information could be added;
	 * {@code null} if the argument was {@code null}
	 */
	@Nullable
	public static Throwable tryEnrichTaskManagerError(@Nullable Throwable exception) {
		return tryEnrichOutOfMemoryError(exception, TM_METASPACE_OOM_ERROR_MESSAGE, TM_DIRECT_OOM_ERROR_MESSAGE);
	}
}
