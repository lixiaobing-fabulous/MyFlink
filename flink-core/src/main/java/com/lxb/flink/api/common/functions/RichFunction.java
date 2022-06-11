package com.lxb.flink.api.common.functions;

import com.lxb.flink.configuration.Configuration;

public interface RichFunction extends Function {
    void open(Configuration parameters) throws Exception;

    void close() throws Exception;

    RuntimeContext getRuntimeContext();

    void setRuntimeContext(RuntimeContext runtimeContext);
}
