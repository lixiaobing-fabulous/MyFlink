package com.lxb.flink.api.java.tupple;

public class Tuple3<T0, T1, T2> extends Tuple {
    public T0 f0;
    public T1 f1;
    public T2 f2;

    public Tuple3() {
    }

    public Tuple3(T0 value0, T1 value1, T2 value2) {
        this.f0 = value0;
        this.f1 = value1;
        this.f2 = value2;
    }

}
