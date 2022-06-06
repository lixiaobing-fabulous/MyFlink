package com.lxb.flink.utl;

public interface Collector<T> {

    void collect(T record);

    void close();
}
