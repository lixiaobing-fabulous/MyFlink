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

package com.lxb.flink.core.fs;


import static com.lxb.flink.utl.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;


import com.lxb.flink.annotation.Internal;
import com.lxb.flink.annotation.VisibleForTesting;
import com.lxb.flink.utl.ExceptionUtils;
import com.lxb.flink.utl.RefCounted;

/**
 * A reference counted file which is deleted as soon as no caller
 * holds a reference to the wrapped {@link File}.
 */
@Internal
public class RefCountedFile implements RefCounted {

	private final File file;

	private final AtomicInteger references;

	protected boolean closed;

	public RefCountedFile(final File file) {
		this.file = checkNotNull(file);
		this.references = new AtomicInteger(1);
		this.closed = false;
	}

	public File getFile() {
		return file;
	}

	@Override
	public void retain() {
		references.incrementAndGet();
	}

	@Override
	public boolean release() {
		if (references.decrementAndGet() == 0) {
			return tryClose();
		}
		return false;
	}

	private boolean tryClose() {
		try {
			Files.deleteIfExists(file.toPath());
			return true;
		} catch (Throwable t) {
			ExceptionUtils.rethrowIfFatalError(t);
		}
		return false;
	}

	private void requireOpened() throws IOException {
		if (closed) {
			throw new IOException("Stream closed.");
		}
	}

	@VisibleForTesting
	public int getReferenceCounter() {
		return references.get();
	}
}
