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

package com.lxb.flink.runtime.tasks;

import com.lxb.flink.annotation.Internal;
import com.lxb.flink.runtime.jobgraph.OperatorID;
import com.lxb.flink.runtime.jobgraph.tasks.TaskOperatorEventGateway;
import com.lxb.flink.runtime.operators.coordination.OperatorEvent;
import com.lxb.flink.runtime.operators.coordination.OperatorEventDispatcher;
import com.lxb.flink.runtime.operators.coordination.OperatorEventHandler;
import com.lxb.flink.utl.FlinkException;
import com.lxb.flink.utl.SerializedValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

/**
 * An implementation of the {@link OperatorEventDispatcher}.
 *
 * <p>This class is intended for single threaded use from the stream task mailbox.
 */
@Internal
final class OperatorEventDispatcherImpl implements OperatorEventDispatcher {

	private final Map<OperatorID, OperatorEventHandler> handlers;

	private final ClassLoader classLoader;

	private final TaskOperatorEventGateway toCoordinator;

	OperatorEventDispatcherImpl(ClassLoader classLoader, TaskOperatorEventGateway toCoordinator) {
		this.classLoader = checkNotNull(classLoader);
		this.toCoordinator = checkNotNull(toCoordinator);
		this.handlers = new HashMap<>();
	}

	void dispatchEventToHandlers(OperatorID operatorID, SerializedValue<OperatorEvent> serializedEvent) throws FlinkException {
		final OperatorEvent evt;
		try {
			evt = serializedEvent.deserializeValue(classLoader);
		}
		catch (IOException | ClassNotFoundException e) {
			throw new FlinkException("Could not deserialize operator event", e);
		}
	}

	@Override
	public void registerEventHandler(OperatorID operator, OperatorEventHandler handler) {
		final OperatorEventHandler prior = handlers.putIfAbsent(operator, handler);
		if (prior != null) {
			throw new IllegalStateException("already a handler registered for this operatorId");
		}
	}
}
