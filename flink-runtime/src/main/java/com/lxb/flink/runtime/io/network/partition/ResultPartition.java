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

package com.lxb.flink.runtime.io.network.partition;

import com.lxb.flink.runtime.checkpoint.channel.ChannelStateReader;
import com.lxb.flink.runtime.io.network.api.writer.ResultPartitionWriter;
import com.lxb.flink.runtime.io.network.buffer.BufferBuilder;
import com.lxb.flink.runtime.io.network.buffer.BufferConsumer;
import com.lxb.flink.runtime.io.network.buffer.BufferPool;
import com.lxb.flink.runtime.io.network.buffer.BufferPoolOwner;
import com.lxb.flink.utl.Preconditions;
import com.lxb.flink.utl.function.FunctionWithException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.lxb.flink.utl.Preconditions.checkArgument;
import static com.lxb.flink.utl.Preconditions.checkElementIndex;
import static com.lxb.flink.utl.Preconditions.checkNotNull;
import static com.lxb.flink.utl.Preconditions.checkState;


public class ResultPartition implements ResultPartitionWriter, BufferPoolOwner {

	protected static final Logger LOG = LoggerFactory.getLogger(ResultPartition.class);

	private final String owningTaskName;

	private final int partitionIndex;

	protected final ResultPartitionID partitionId;

	/** Type of this partition. Defines the concrete subpartition implementation to use. */
	protected final ResultPartitionType partitionType;

	/** The subpartitions of this partition. At least one. */
	protected final ResultSubpartition[] subpartitions;

	protected final ResultPartitionManager partitionManager;

	public final int numTargetKeyGroups;

	// - Runtime state --------------------------------------------------------

	private final AtomicBoolean isReleased = new AtomicBoolean();

	private BufferPool bufferPool;

	private boolean isFinished;

	private volatile Throwable cause;

	private final FunctionWithException<BufferPoolOwner, BufferPool, IOException> bufferPoolFactory;

	public ResultPartition(
		String owningTaskName,
		int partitionIndex,
		ResultPartitionID partitionId,
		ResultPartitionType partitionType,
		ResultSubpartition[] subpartitions,
		int numTargetKeyGroups,
		ResultPartitionManager partitionManager,
		FunctionWithException<BufferPoolOwner, BufferPool, IOException> bufferPoolFactory) {

		this.owningTaskName = checkNotNull(owningTaskName);
		Preconditions.checkArgument(0 <= partitionIndex, "The partition index must be positive.");
		this.partitionIndex = partitionIndex;
		this.partitionId = checkNotNull(partitionId);
		this.partitionType = checkNotNull(partitionType);
		this.subpartitions = checkNotNull(subpartitions);
		this.numTargetKeyGroups = numTargetKeyGroups;
		this.partitionManager = checkNotNull(partitionManager);
		this.bufferPoolFactory = bufferPoolFactory;
	}

	/**
	 * Registers a buffer pool with this result partition.
	 *
	 * <p>There is one pool for each result partition, which is shared by all its sub partitions.
	 *
	 * <p>The pool is registered with the partition *after* it as been constructed in order to conform
	 */
	@Override
	public void setup() throws IOException {
		checkState(this.bufferPool == null, "Bug in result partition setup logic: Already registered buffer pool.");

		BufferPool bufferPool = checkNotNull(bufferPoolFactory.apply(this));
		checkArgument(bufferPool.getNumberOfRequiredMemorySegments() >= getNumberOfSubpartitions(),
			"Bug in result partition setup logic: Buffer pool has not enough guaranteed buffers for this result partition.");

		this.bufferPool = bufferPool;
		partitionManager.registerResultPartition(this);
	}

	@Override
	public void readRecoveredState(ChannelStateReader stateReader) throws IOException, InterruptedException {
		for (ResultSubpartition subpartition : subpartitions) {
			subpartition.readRecoveredState(stateReader);
		}
		LOG.debug("{}: Finished reading recovered state.", this);
	}

	public String getOwningTaskName() {
		return owningTaskName;
	}

	public ResultPartitionID getPartitionId() {
		return partitionId;
	}

	public int getPartitionIndex() {
		return partitionIndex;
	}

	@Override
	public ResultSubpartition getSubpartition(int subpartitionIndex) {
		return subpartitions[subpartitionIndex];
	}

	@Override
	public int getNumberOfSubpartitions() {
		return subpartitions.length;
	}

	public BufferPool getBufferPool() {
		return bufferPool;
	}

	public int getNumberOfQueuedBuffers() {
		int totalBuffers = 0;

		for (ResultSubpartition subpartition : subpartitions) {
			totalBuffers += subpartition.unsynchronizedGetNumberOfQueuedBuffers();
		}

		return totalBuffers;
	}

	/**
	 * Returns the type of this result partition.
	 *
	 * @return result partition type
	 */
	public ResultPartitionType getPartitionType() {
		return partitionType;
	}

	// ------------------------------------------------------------------------

	@Override
	public BufferBuilder getBufferBuilder(int targetChannel) throws IOException, InterruptedException {
		checkInProduceState();

		return bufferPool.requestBufferBuilderBlocking(targetChannel);
	}

	@Override
	public BufferBuilder tryGetBufferBuilder(int targetChannel) throws IOException {
		BufferBuilder bufferBuilder = bufferPool.requestBufferBuilder(targetChannel);
		return bufferBuilder;
	}

	@Override
	public boolean addBufferConsumer(
			BufferConsumer bufferConsumer,
			int subpartitionIndex,
			boolean isPriorityEvent) throws IOException {
		checkNotNull(bufferConsumer);

		ResultSubpartition subpartition;
		try {
			checkInProduceState();
			subpartition = subpartitions[subpartitionIndex];
		}
		catch (Exception ex) {
			bufferConsumer.close();
			throw ex;
		}

		return subpartition.add(bufferConsumer, isPriorityEvent);
	}

	@Override
	public void flushAll() {
		for (ResultSubpartition subpartition : subpartitions) {
			subpartition.flush();
		}
	}

	@Override
	public void flush(int subpartitionIndex) {
		subpartitions[subpartitionIndex].flush();
	}

	/**
	 * Finishes the result partition.
	 *
	 * <p>After this operation, it is not possible to add further data to the result partition.
	 *
	 * <p>For BLOCKING results, this will trigger the deployment of consuming tasks.
	 */
	@Override
	public void finish() throws IOException {
		checkInProduceState();

		for (ResultSubpartition subpartition : subpartitions) {
			subpartition.finish();
		}

		isFinished = true;
	}

	public void release() {
		release(null);
	}

	/**
	 * Releases the result partition.
	 */
	public void release(Throwable cause) {
		if (isReleased.compareAndSet(false, true)) {
			LOG.debug("{}: Releasing {}.", owningTaskName, this);

			// Set the error cause
			if (cause != null) {
				this.cause = cause;
			}

			// Release all subpartitions
			for (ResultSubpartition subpartition : subpartitions) {
				try {
					subpartition.release();
				}
				// Catch this in order to ensure that release is called on all subpartitions
				catch (Throwable t) {
					LOG.error("Error during release of result subpartition: " + t.getMessage(), t);
				}
			}
		}
	}

	@Override
	public void close() {
		if (bufferPool != null) {
			bufferPool.lazyDestroy();
		}
	}

	@Override
	public void fail(@Nullable Throwable throwable) {
		partitionManager.releasePartition(partitionId, throwable);
	}

	/**
	 * Returns the requested subpartition.
	 */
	public ResultSubpartitionView createSubpartitionView(int index, BufferAvailabilityListener availabilityListener) throws IOException {
		checkElementIndex(index, subpartitions.length, "Subpartition not found.");
		checkState(!isReleased.get(), "Partition released.");

		ResultSubpartitionView readView = subpartitions[index].createReadView(availabilityListener);

		LOG.debug("Created {}", readView);

		return readView;
	}

	public Throwable getFailureCause() {
		return cause;
	}

	@Override
	public int getNumTargetKeyGroups() {
		return numTargetKeyGroups;
	}

	/**
	 * Releases buffers held by this result partition.
	 *
	 * <p>This is a callback from the buffer pool, which is registered for result partitions, which
	 * are back pressure-free.
	 */
	@Override
	public void releaseMemory(int toRelease) throws IOException {
		checkArgument(toRelease > 0);

		for (ResultSubpartition subpartition : subpartitions) {
			toRelease -= subpartition.releaseMemory();

			// Only release as much memory as needed
			if (toRelease <= 0) {
				break;
			}
		}
	}

	/**
	 * Whether this partition is released.
	 *
	 * <p>A partition is released when each subpartition is either consumed and communication is closed by consumer
	 * or failed. A partition is also released if task is cancelled.
	 */
	public boolean isReleased() {
		return isReleased.get();
	}

	@Override
	public CompletableFuture<?> getAvailableFuture() {
		return bufferPool.getAvailableFuture();
	}

	@Override
	public String toString() {
		return "ResultPartition " + partitionId.toString() + " [" + partitionType + ", "
				+ subpartitions.length + " subpartitions]";
	}

	// ------------------------------------------------------------------------

	/**
	 * Notification when a subpartition is released.
	 */
	void onConsumedSubpartition(int subpartitionIndex) {

		if (isReleased.get()) {
			return;
		}

		LOG.debug("{}: Received release notification for subpartition {}.",
				this, subpartitionIndex);
	}

	public ResultSubpartition[] getAllPartitions() {
		return subpartitions;
	}

	// ------------------------------------------------------------------------

	private void checkInProduceState() throws IllegalStateException {
		checkState(!isFinished, "Partition already finished.");
	}
}
