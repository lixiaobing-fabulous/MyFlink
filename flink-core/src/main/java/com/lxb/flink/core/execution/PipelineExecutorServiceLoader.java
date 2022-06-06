package com.lxb.flink.core.execution;

import com.lxb.flink.configuration.Configuration;

import java.util.stream.Stream;

public interface PipelineExecutorServiceLoader {

    PipelineExecutorFactory getExecutorFactory(final Configuration configuration) throws Exception;

    Stream<String> getExecutorNames();

}
