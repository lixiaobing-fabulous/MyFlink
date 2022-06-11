package com.lxb.flink.api.common.typeinfo;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class TypeInfoFactory<T> {
    public abstract TypeInformation<T> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters);

}
