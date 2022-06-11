package com.lxb.flink.api.common.typeuitils;

public abstract class TypePairComparator<T1, T2> {

    public abstract void setReference(T1 reference);

    public abstract boolean equalToReference(T2 candidate);

    public abstract int compareToReference(T2 candidate);

}
