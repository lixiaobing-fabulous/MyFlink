package com.lxb.flink.api.operators;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeinfo.TypeInformation;

import java.io.Serializable;

public interface StreamOperatorFactory<OUT> extends Serializable {

    default boolean isStreamSource() {
        return false;
    }

    default boolean isOutputTypeConfigurable() {
        return false;
    }

    default void setOutputType(TypeInformation<OUT> type, ExecutionConfig executionConfig) {}

    void setChainingStrategy(ChainingStrategy strategy);

}
