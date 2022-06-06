package com.lxb.flink.runtime.partitioner;

import com.lxb.flink.runtime.plugable.SerializationDelegate;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

import java.util.concurrent.ThreadLocalRandom;

public class RebalancePartitioner<T> extends StreamPartitioner<T> {
    private int nextChannelToSendTo;

    @Override
    public void setup(int numberOfChannels) {
        super.setup(numberOfChannels);

        nextChannelToSendTo = ThreadLocalRandom.current().nextInt(numberOfChannels);
    }

    @Override
    public int selectChannel(SerializationDelegate<StreamRecord<T>> record) {
        nextChannelToSendTo = (nextChannelToSendTo + 1) % numberOfChannels;
        return nextChannelToSendTo;
    }

    @Override
    public StreamPartitioner<T> copy() {
        return this;
    }

    @Override
    public String toString() {
        return "REBALANCE";
    }
}
