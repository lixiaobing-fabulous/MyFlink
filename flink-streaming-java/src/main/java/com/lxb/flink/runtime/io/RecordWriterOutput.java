/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lxb.flink.runtime.io;

import static com.lxb.flink.utl.Preconditions.checkNotNull;

import java.io.IOException;

import com.lxb.flink.annotation.Internal;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.api.watermark.Watermark;
import com.lxb.flink.runtime.io.network.api.writer.RecordWriter;
import com.lxb.flink.runtime.metrics.WatermarkGauge;
import com.lxb.flink.runtime.streamrecord.LatencyMarker;
import com.lxb.flink.runtime.streamrecord.StreamElementSerializer;
import com.lxb.flink.runtime.streamstatus.StreamStatus;
import com.lxb.flink.runtime.streamstatus.StreamStatusProvider;
import com.lxb.flink.utl.OutputTag;

import com.lxb.flink.runtime.plugable.SerializationDelegate;
import com.lxb.flink.runtime.streamrecord.StreamElement;
import com.lxb.flink.runtime.streamrecord.StreamRecord;
import com.lxb.flink.runtime.tasks.OperatorChain;

/**
 */
@Internal
public class RecordWriterOutput<OUT> implements OperatorChain.WatermarkGaugeExposingOutput<StreamRecord<OUT>> {

	private RecordWriter<SerializationDelegate<StreamElement>> recordWriter;

	private SerializationDelegate<StreamElement> serializationDelegate;

	private final StreamStatusProvider streamStatusProvider;

	private final OutputTag outputTag;

	private final WatermarkGauge watermarkGauge = new WatermarkGauge();

	@SuppressWarnings("unchecked")
	public RecordWriterOutput(
			RecordWriter<SerializationDelegate<StreamRecord<OUT>>> recordWriter,
			TypeSerializer<OUT> outSerializer,
			OutputTag outputTag,
			StreamStatusProvider streamStatusProvider) {

		checkNotNull(recordWriter);
		this.outputTag = outputTag;
		// generic hack: cast the writer to generic Object type so we can use it
		// with multiplexed records and watermarks
		this.recordWriter = (RecordWriter<SerializationDelegate<StreamElement>>)
				(RecordWriter<?>) recordWriter;

		TypeSerializer<StreamElement> outRecordSerializer =
				new StreamElementSerializer<>(outSerializer);

		if (outSerializer != null) {
			serializationDelegate = new SerializationDelegate<StreamElement>(outRecordSerializer);
		}

		this.streamStatusProvider = checkNotNull(streamStatusProvider);
	}

	@Override
	public void collect(StreamRecord<OUT> record) {
		if (this.outputTag != null) {
			// we are not responsible for emitting to the main output.
			return;
		}

		pushToRecordWriter(record);
	}

	@Override
	public <X> void collect(OutputTag<X> outputTag, StreamRecord<X> record) {
		if (this.outputTag == null || !this.outputTag.equals(outputTag)) {
			// we are not responsible for emitting to the side-output specified by this
			// OutputTag.
			return;
		}

		pushToRecordWriter(record);
	}

	private <X> void pushToRecordWriter(StreamRecord<X> record) {
		serializationDelegate.setInstance(record);

		try {
			recordWriter.emit(serializationDelegate);
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void emitWatermark(Watermark mark) {
		watermarkGauge.setCurrentWatermark(mark.getTimestamp());
		serializationDelegate.setInstance(mark);

		if (streamStatusProvider.getStreamStatus().isActive()) {
			try {
				recordWriter.broadcastEmit(serializationDelegate);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	public void emitStreamStatus(StreamStatus streamStatus) {
		serializationDelegate.setInstance(streamStatus);

		try {
			recordWriter.broadcastEmit(serializationDelegate);
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void emitLatencyMarker(LatencyMarker latencyMarker) {
		serializationDelegate.setInstance(latencyMarker);

		try {
			recordWriter.randomEmit(serializationDelegate);
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void flush() throws IOException {
		recordWriter.flushAll();
	}

	@Override
	public void close() {
		recordWriter.close();
	}

}
