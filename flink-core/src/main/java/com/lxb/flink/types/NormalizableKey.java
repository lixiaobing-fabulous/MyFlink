package com.lxb.flink.types;

import com.lxb.flink.core.memory.MemorySegment;

public interface NormalizableKey<T> extends Comparable<T>, Key<T> {
    int getMaxNormalizedKeyLen();

    void copyNormalizedKey(MemorySegment memory, int offset, int len);

}
