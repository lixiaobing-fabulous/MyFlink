package com.lxb.flink.api.common.typeinfo;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;

import java.io.Serializable;

public abstract class TypeInformation<T> implements Serializable {

    public abstract TypeSerializer<T> createSerializer(ExecutionConfig config);

}
