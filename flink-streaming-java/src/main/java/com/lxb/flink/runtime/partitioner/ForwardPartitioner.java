package com.lxb.flink.runtime.partitioner;

import com.lxb.flink.runtime.plugable.SerializationDelegate;
import com.lxb.flink.runtime.streamrecord.StreamRecord;

public class ForwardPartitioner<T> extends StreamPartitioner<T> {
    @Override
    public int selectChannel(SerializationDelegate<StreamRecord<T>> record) {
        return 0;
    }

    @Override
    public StreamPartitioner<T> copy() {
        return this;
    }

    @Override
    public String toString() {
        return "FORWARD";
    }

}
