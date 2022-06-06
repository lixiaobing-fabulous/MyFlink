package com.lxb.flink.core.execution;

import com.lxb.flink.api.common.JobExecutionResult;
import com.lxb.flink.api.common.JobID;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface JobClient {

    JobID getJobID();

    CompletableFuture<Map<String, Object>> getAccumulators(ClassLoader classLoader);

    CompletableFuture<JobExecutionResult> getJobExecutionResult(final ClassLoader userClassloader);

}
