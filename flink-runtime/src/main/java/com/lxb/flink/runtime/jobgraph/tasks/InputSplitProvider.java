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

package com.lxb.flink.runtime.jobgraph.tasks;


import com.lxb.flink.annotation.Public;
import com.lxb.flink.core.io.InputSplit;

/**
 * An input split provider can be successively queried to provide a series of {@link InputSplit} objects a
 * task is supposed to consume in the course of its execution.
 */
@Public
public interface InputSplitProvider {

		InputSplit getNextInputSplit(ClassLoader userCodeClassLoader) throws InputSplitProviderException;
}
