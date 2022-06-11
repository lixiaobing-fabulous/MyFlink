package com.lxb.flink.core.memory;

public abstract class MemorySegment {
    public abstract void put(int index, byte b);

    public abstract void putIntBigEndian(int offset, int i);

    public abstract void putLongBigEndian(int offset, long l);
}
