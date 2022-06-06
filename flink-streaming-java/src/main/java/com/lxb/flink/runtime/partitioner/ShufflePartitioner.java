package com.lxb.flink.runtime.partitioner;

import com.lxb.flink.runtime.plugable.SerializationDelegate;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

import java.util.Random;

public class ShufflePartitioner<T> extends StreamPartitioner<T> {
    private Random random = new Random();

    @Override
    public int selectChannel(SerializationDelegate<StreamRecord<T>> record) {
        return random.nextInt(numberOfChannels);
    }

    @Override
    public StreamPartitioner<T> copy() {
        return new ShufflePartitioner<T>();
    }

    @Override
    public String toString() {
        return "SHUFFLE";
    }
}
