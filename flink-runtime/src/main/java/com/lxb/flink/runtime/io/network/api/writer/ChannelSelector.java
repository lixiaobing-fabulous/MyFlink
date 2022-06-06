package com.lxb.flink.runtime.io.network.api.writer;

import com.lxb.flink.core.io.IOReadableWritable;

public interface ChannelSelector<T extends IOReadableWritable> {

    void setup(int numberOfChannels);

    int selectChannel(T record);

    boolean isBroadcast();

}
