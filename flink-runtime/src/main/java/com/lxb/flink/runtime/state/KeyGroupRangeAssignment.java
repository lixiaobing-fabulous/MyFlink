package com.lxb.flink.runtime.state;

import com.lxb.flink.utl.MathUtils;

public class KeyGroupRangeAssignment {
    public static <K> int assignKeyToParallelOperator(K key, int maxParallelism, int parallelism) {
        return computeOperatorIndexForKeyGroup(maxParallelism, parallelism, assignToKeyGroup(key, maxParallelism));
    }

    public static int computeOperatorIndexForKeyGroup(int maxParallelism, int parallelism, int keyGroupId) {
        return keyGroupId * parallelism / maxParallelism;
    }

    public static int assignToKeyGroup(Object key, int maxParallelism) {
        return computeKeyGroupForKeyHash(key.hashCode(), maxParallelism);
    }

    public static int computeKeyGroupForKeyHash(int keyHash, int maxParallelism) {
        return MathUtils.murmurHash(keyHash) % maxParallelism;
    }


    public static void checkParallelismPreconditions(int maxParallelism) {
    }
}
