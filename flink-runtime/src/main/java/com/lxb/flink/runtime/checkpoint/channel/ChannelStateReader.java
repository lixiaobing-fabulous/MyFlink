package com.lxb.flink.runtime.checkpoint.channel;

import java.io.IOException;

import com.lxb.flink.annotation.Internal;
import com.lxb.flink.runtime.io.network.buffer.Buffer;
import com.lxb.flink.runtime.io.network.buffer.BufferBuilder;


@Internal
public interface ChannelStateReader extends AutoCloseable {

	enum ReadResult { HAS_MORE_DATA, NO_MORE_DATA }

	boolean hasChannelStates();

	ReadResult readInputData(InputChannelInfo info, Buffer buffer) throws IOException;

	ReadResult readOutputData(ResultSubpartitionInfo info, BufferBuilder bufferBuilder) throws IOException;

	@Override
	void close() throws Exception;

}
