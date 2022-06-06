package com.lxb.flink.core.execution;

import com.lxb.flink.api.dag.Pipeline;
import com.lxb.flink.configuration.Configuration;

import java.util.concurrent.CompletableFuture;

public interface PipelineExecutor {

    CompletableFuture<JobClient> execute(final Pipeline pipeline, final Configuration configuration) throws Exception;

}
