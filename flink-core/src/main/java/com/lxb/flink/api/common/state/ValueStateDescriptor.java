package com.lxb.flink.api.common.state;

import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;

public class ValueStateDescriptor<T> extends StateDescriptor<ValueState<T>, T> {
    public ValueStateDescriptor(String name, Class<T> typeClass, T defaultValue) {
        super(name, typeClass, defaultValue);
    }

    public ValueStateDescriptor(String name, TypeInformation<T> typeInfo, T defaultValue) {
        super(name, typeInfo, defaultValue);
    }

    public ValueStateDescriptor(String name, TypeSerializer<T> typeSerializer, T defaultValue) {
        super(name, typeSerializer, defaultValue);
    }

    public ValueStateDescriptor(String name, Class<T> typeClass) {
        super(name, typeClass, null);
    }

    public ValueStateDescriptor(String name, TypeInformation<T> typeInfo) {
        super(name, typeInfo, null);
    }

    public ValueStateDescriptor(String name, TypeSerializer<T> typeSerializer) {
        super(name, typeSerializer, null);
    }


    @Override
    public Type getType() {
        return Type.VALUE;
    }
}
