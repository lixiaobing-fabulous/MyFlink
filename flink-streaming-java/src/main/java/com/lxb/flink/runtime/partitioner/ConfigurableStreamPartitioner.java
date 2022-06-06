package com.lxb.flink.runtime.partitioner;

public interface ConfigurableStreamPartitioner {

    void configure(int maxParallelism);

}
