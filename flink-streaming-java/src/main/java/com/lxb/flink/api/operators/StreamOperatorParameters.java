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

package com.lxb.flink.api.operators;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.lxb.flink.annotation.Experimental;
import com.lxb.flink.api.graph.StreamConfig;
import com.lxb.flink.runtime.operators.coordination.OperatorEventDispatcher;
import com.lxb.flink.runtime.streamrecord.StreamRecord;
import com.lxb.flink.runtime.tasks.ProcessingTimeService;
import com.lxb.flink.runtime.tasks.StreamTask;


@Experimental
public class StreamOperatorParameters<OUT> {
	private final StreamTask<?, ?> containingTask;
	private final StreamConfig config;
	private final Output<StreamRecord<OUT>> output;
	private final Supplier<ProcessingTimeService> processingTimeServiceFactory;
	private final OperatorEventDispatcher operatorEventDispatcher;

	/** The ProcessingTimeService, lazily created, but cached so that we don't create more than one. */
	@Nullable
	private ProcessingTimeService processingTimeService;

	public StreamOperatorParameters(
			StreamTask<?, ?> containingTask,
			StreamConfig config,
			Output<StreamRecord<OUT>> output,
			Supplier<ProcessingTimeService> processingTimeServiceFactory,
			OperatorEventDispatcher operatorEventDispatcher) {
		this.containingTask = containingTask;
		this.config = config;
		this.output = output;
		this.processingTimeServiceFactory = processingTimeServiceFactory;
		this.operatorEventDispatcher = operatorEventDispatcher;
	}

	public StreamTask<?, ?> getContainingTask() {
		return containingTask;
	}

	public StreamConfig getStreamConfig() {
		return config;
	}

	public Output<StreamRecord<OUT>> getOutput() {
		return output;
	}

	public ProcessingTimeService getProcessingTimeService() {
		if (processingTimeService == null) {
			processingTimeService = processingTimeServiceFactory.get();
		}
		return processingTimeService;
	}

	public OperatorEventDispatcher getOperatorEventDispatcher() {
		return operatorEventDispatcher;
	}
}
