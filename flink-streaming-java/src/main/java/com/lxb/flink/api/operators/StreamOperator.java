package com.lxb.flink.api.operators;

import com.lxb.flink.utl.Disposable;

import java.io.Serializable;

public interface StreamOperator<OUT> extends KeyContext, Disposable, Serializable {
    void open() throws Exception;
    void close() throws Exception;
    @Override
    void dispose() throws Exception;

}
