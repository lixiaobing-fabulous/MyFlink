/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lxb.flink.runtime.streamstatus;


import com.lxb.flink.annotation.Internal;
import com.lxb.flink.runtime.streamrecord.StreamElement;


@Internal
public final class StreamStatus extends StreamElement {

	public static final int IDLE_STATUS = -1;
	public static final int ACTIVE_STATUS = 0;

	public static final StreamStatus IDLE = new StreamStatus(IDLE_STATUS);
	public static final StreamStatus ACTIVE = new StreamStatus(ACTIVE_STATUS);

	public final int status;

	public StreamStatus(int status) {
		if (status != IDLE_STATUS && status != ACTIVE_STATUS) {
			throw new IllegalArgumentException("Invalid status value for StreamStatus; " +
				"allowed values are " + ACTIVE_STATUS + " (for ACTIVE) and " + IDLE_STATUS + " (for IDLE).");
		}

		this.status = status;
	}

	public boolean isIdle() {
		return this.status == IDLE_STATUS;
	}

	public boolean isActive() {
		return !isIdle();
	}

	public int getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object o) {
		return this == o ||
			o != null && o.getClass() == StreamStatus.class && ((StreamStatus) o).status == this.status;
	}

	@Override
	public int hashCode() {
		return status;
	}

	@Override
	public String toString() {
		String statusStr = (status == ACTIVE_STATUS) ? "ACTIVE" : "IDLE";
		return "StreamStatus(" + statusStr + ")";
	}
}
