package com.lxb.flink.api.operators;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.typeinfo.TypeInformation;

import java.io.Serializable;

public interface StreamOperatorFactory<OUT> extends Serializable {

    <T extends StreamOperator<OUT>> T createStreamOperator(StreamOperatorParameters<OUT> parameters);

    void setChainingStrategy(ChainingStrategy strategy);

    ChainingStrategy getChainingStrategy();

    default boolean isStreamSource() {
        return false;
    }

    default boolean isOutputTypeConfigurable() {
        return false;
    }

    default void setOutputType(TypeInformation<OUT> type, ExecutionConfig executionConfig) {
    }

    default boolean isInputTypeConfigurable() {
        return false;
    }

    default void setInputType(TypeInformation<?> type, ExecutionConfig executionConfig) {
    }

    Class<? extends StreamOperator> getStreamOperatorClass(ClassLoader classLoader);
}
