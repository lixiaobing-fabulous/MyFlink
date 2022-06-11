package com.lxb.flink.api.common.functions;

import java.io.Serializable;

public interface Partitioner<K> extends Serializable, Function {

    int partition(K key, int numPartitions);
}
