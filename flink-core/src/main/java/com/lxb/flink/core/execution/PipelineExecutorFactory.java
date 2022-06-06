package com.lxb.flink.core.execution;

import com.lxb.flink.configuration.Configuration;

public interface PipelineExecutorFactory {
    String getName();

    boolean isCompatibleWith(final Configuration configuration);

    PipelineExecutor getExecutor(final Configuration configuration);

}
