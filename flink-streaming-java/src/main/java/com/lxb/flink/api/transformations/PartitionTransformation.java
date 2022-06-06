package com.lxb.flink.api.transformations;

import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.runtime.partitioner.StreamPartitioner;

public class PartitionTransformation<T> extends Transformation<T> {

    private final Transformation<T> input;

    private final StreamPartitioner<T> partitioner;

    private final ShuffleMode shuffleMode;

    public PartitionTransformation(Transformation<T> input, StreamPartitioner<T> partitioner) {
        this(input, partitioner, ShuffleMode.UNDEFINED);
    }

    public PartitionTransformation(
            Transformation<T> input,
            StreamPartitioner<T> partitioner,
            ShuffleMode shuffleMode) {
        super("Partition", input.getOutputType(), input.getParallelism());
        this.input = input;
        this.partitioner = partitioner;
        this.shuffleMode = shuffleMode;
    }

    public Transformation<T> getInput() {
        return input;
    }

    public StreamPartitioner<T> getPartitioner() {
        return partitioner;
    }

    public ShuffleMode getShuffleMode() {
        return shuffleMode;
    }
}
