package com.lxb.flink.api.java.typeutils;

import com.lxb.flink.api.common.typeinfo.TypeInformation;

public interface ResultTypeQueryable<T> {
    TypeInformation<T> getProducedType();

}
