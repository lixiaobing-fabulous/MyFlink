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

package com.lxb.flink.runtime.io.network.api.writer;

import com.lxb.flink.core.io.IOReadableWritable;
import com.lxb.flink.runtime.io.AvailabilityProvider;
import org.apache.flink.core.io.IOReadableWritable;
import org.apache.flink.runtime.io.AvailabilityProvider;

/**
 * The record writer delegate provides the availability function for task processor, and it might represent
 * a single {@link RecordWriter} or multiple {@link RecordWriter} instances in specific implementations.
 */
public interface RecordWriterDelegate<T extends IOReadableWritable> extends AvailabilityProvider, AutoCloseable {
	RecordWriter<T> getRecordWriter(int outputIndex);
}