package com.lxb.flink.runtime.partitioner;

import com.lxb.flink.runtime.io.network.api.writer.ChannelSelector;
import com.lxb.flink.runtime.plugable.SerializationDelegate;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

import java.io.Serializable;

public abstract class StreamPartitioner<T> implements ChannelSelector<SerializationDelegate<StreamRecord<T>>>, Serializable {
    protected int numberOfChannels;

    @Override
    public void setup(int numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    public abstract StreamPartitioner<T> copy();

    @Override
    public boolean isBroadcast() {
        return false;
    }
}
