package com.lxb.flink.types;

public interface Key<T> extends Value, Comparable<T> {
    int hashCode();

    boolean equals(Object other);
}
