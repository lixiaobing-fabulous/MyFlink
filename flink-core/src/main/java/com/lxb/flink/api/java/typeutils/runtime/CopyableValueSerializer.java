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

package com.lxb.flink.api.java.typeutils.runtime;

import com.lxb.flink.annotation.Internal;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;
import com.lxb.flink.types.CopyableValue;
import com.lxb.flink.utl.InstantiationUtil;

import java.io.IOException;

import static com.lxb.flink.utl.Preconditions.checkNotNull;


@Internal
public final class CopyableValueSerializer<T extends CopyableValue<T>> extends TypeSerializer<T> {

	private static final long serialVersionUID = 1L;

	private final Class<T> valueClass;

	private transient T instance;

	public CopyableValueSerializer(Class<T> valueClass) {
		this.valueClass = checkNotNull(valueClass);
	}

	private Class<T> getValueClass() {
		return valueClass;
	}

	@Override
	public boolean isImmutableType() {
		return false;
	}

	@Override
	public CopyableValueSerializer<T> duplicate() {
		return this;
	}

	@Override
	public T createInstance() {
		return InstantiationUtil.instantiate(this.valueClass);
	}

	@Override
	public T copy(T from) {
		return copy(from, createInstance());
	}

	@Override
	public T copy(T from, T reuse) {
		from.copyTo(reuse);
		return reuse;
	}

	@Override
	public int getLength() {
		ensureInstanceInstantiated();
		return instance.getBinaryLength();
	}

	@Override
	public void serialize(T value, DataOutputView target) throws IOException {
		value.write(target);
	}

	@Override
	public T deserialize(DataInputView source) throws IOException {
		return deserialize(createInstance(), source);
	}

	@Override
	public T deserialize(T reuse, DataInputView source) throws IOException {
		reuse.read(source);
		return reuse;
	}

	@Override
	public void copy(DataInputView source, DataOutputView target) throws IOException {
		ensureInstanceInstantiated();
		instance.copy(source, target);
	}

	// --------------------------------------------------------------------------------------------

	private void ensureInstanceInstantiated() {
		if (instance == null) {
			instance = createInstance();
		}
	}

	@Override
	public int hashCode() {
		return this.valueClass.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CopyableValueSerializer) {
			@SuppressWarnings("unchecked")
			CopyableValueSerializer<T> copyableValueSerializer = (CopyableValueSerializer<T>) obj;

			return valueClass == copyableValueSerializer.valueClass;
		} else {
			return false;
		}
	}
}
