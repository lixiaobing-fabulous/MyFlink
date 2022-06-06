package com.lxb.flink.api.common.typeuitils;

import com.lxb.flink.core.memory.DataOutputView;

import java.io.IOException;
import java.io.Serializable;

public abstract class TypeSerializer<T> implements Serializable {

    public abstract void serialize(T record, DataOutputView target) throws IOException;

}
