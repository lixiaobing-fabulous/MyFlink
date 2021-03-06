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
import com.lxb.flink.core.memory.DataInputView;
import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;

/**
 * Utilities for reading and writing binary masks.
 */
@Internal
public final class MaskUtils {

	@SuppressWarnings("UnusedAssignment")
	public static void writeMask(boolean[] mask, DataOutputView target) throws IOException {
		final int len = mask.length;

		int b = 0x00;
		int bytePos = 0;

		int fieldPos = 0;
		int numPos = 0;
		while (fieldPos < len) {
			b = 0x00;
			// set bits in byte
			bytePos = 0;
			numPos = Math.min(8, len - fieldPos);
			while (bytePos < numPos) {
				b = b << 1;
				// set bit if element is true
				if (mask[fieldPos + bytePos]) {
					b |= 0x01;
				}
				bytePos += 1;
			}
			fieldPos += numPos;
			// shift bits if last byte is not completely filled
			b <<= (8 - bytePos);
			// write byte
			target.writeByte(b);
		}
	}

	@SuppressWarnings("UnusedAssignment")
	public static void readIntoMask(DataInputView source, boolean[] mask) throws IOException {
		final int len = mask.length;

		int b = 0x00;
		int bytePos = 0;

		int fieldPos = 0;
		int numPos = 0;
		while (fieldPos < len) {
			// read byte
			b = source.readUnsignedByte();
			bytePos = 0;
			numPos = Math.min(8, len - fieldPos);
			while (bytePos < numPos) {
				mask[fieldPos + bytePos] = (b & 0x80) > 0;
				b = b << 1;
				bytePos += 1;
			}
			fieldPos += numPos;
		}
	}

	@SuppressWarnings("UnusedAssignment")
	public static void readIntoAndCopyMask(
			DataInputView source,
			DataOutputView target,
			boolean[] mask) throws IOException {
		final int len = mask.length;

		int b = 0x00;
		int bytePos = 0;

		int fieldPos = 0;
		int numPos = 0;
		while (fieldPos < len) {
			// read byte
			b = source.readUnsignedByte();
			// copy byte
			target.writeByte(b);
			bytePos = 0;
			numPos = Math.min(8, len - fieldPos);
			while (bytePos < numPos) {
				mask[fieldPos + bytePos] = (b & 0x80) > 0;
				b = b << 1;
				bytePos += 1;
			}
			fieldPos += numPos;
		}
	}
}
