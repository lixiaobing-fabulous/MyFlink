package com.lxb.flink.runtime.partitioner;

import com.lxb.flink.api.java.functions.KeySelector;
import com.lxb.flink.runtime.plugable.SerializationDelegate;
import com.lxb.flink.runtime.state.KeyGroupRangeAssignment;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

public class KeyGroupStreamPartitioner<T, K> extends StreamPartitioner<T> implements ConfigurableStreamPartitioner {
    private final KeySelector<T, K> keySelector;

    private int maxParallelism;

    public KeyGroupStreamPartitioner(KeySelector<T, K> keySelector, int maxParallelism) {
        this.keySelector = keySelector;
        this.maxParallelism = maxParallelism;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    @Override
    public int selectChannel(SerializationDelegate<StreamRecord<T>> record) {
        K key;
        try {
            key = keySelector.getKey(record.getInstance().getValue());
        } catch (Exception e) {
            throw new RuntimeException("Could not extract key from " + record.getInstance().getValue(), e);
        }
        return KeyGroupRangeAssignment.assignKeyToParallelOperator(key, maxParallelism, numberOfChannels);
    }

    @Override
    public void configure(int maxParallelism) {
        KeyGroupRangeAssignment.checkParallelismPreconditions(maxParallelism);

        this.maxParallelism = maxParallelism;
    }

    @Override
    public StreamPartitioner<T> copy() {
        return this;
    }

    @Override
    public String toString() {
        return "HASH";
    }
}
