package com.lxb.flink.core.execution;


import com.lxb.flink.api.common.JobExecutionResult;

public interface JobListener {

    void onJobSubmitted(JobClient jobClient, Throwable throwable);

    void onJobExecuted(JobExecutionResult jobExecutionResult, Throwable throwable);


}
