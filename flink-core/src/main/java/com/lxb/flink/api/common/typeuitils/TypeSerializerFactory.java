package com.lxb.flink.api.common.typeuitils;

import com.lxb.flink.configuration.Configuration;

public interface TypeSerializerFactory<T> {

    void writeParametersToConfig(Configuration config);

    void readParametersFromConfig(Configuration config, ClassLoader cl) throws ClassNotFoundException;

    TypeSerializer<T> getSerializer();

    Class<T> getDataType();

}
