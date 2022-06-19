package com.lxb.flink.api.operators;

import com.lxb.flink.runtime.jobgraph.OperatorID;
import com.lxb.flink.runtime.streamrecord.StreamRecord;
import com.lxb.flink.utl.Disposable;

import java.io.Serializable;

public interface StreamOperator<OUT> extends KeyContext, Disposable, Serializable {
    void open() throws Exception;

    void close() throws Exception;

    @Override
    void dispose() throws Exception;

    void prepareSnapshotPreBarrier(long checkpointId) throws Exception;

    void initializeState(StreamTaskStateInitializer streamTaskStateManager) throws Exception;

    void setKeyContextElement1(StreamRecord<?> record) throws Exception;

    void setKeyContextElement2(StreamRecord<?> record) throws Exception;

    OperatorID getOperatorID();

}
