package com.lxb.flink.api.common.functions;

import com.lxb.flink.configuration.Configuration;

import java.io.Serializable;

public abstract class AbstractRichFunction implements RichFunction, Serializable {
    private transient RuntimeContext runtimeContext;

    @Override
    public void setRuntimeContext(RuntimeContext t) {
        this.runtimeContext = t;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        if (this.runtimeContext != null) {
            return this.runtimeContext;
        } else {
            throw new IllegalStateException("The runtime context has not been initialized.");
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

}
