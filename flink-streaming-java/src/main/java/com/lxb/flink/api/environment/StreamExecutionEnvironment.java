package com.lxb.flink.api.environment;

import com.lxb.flink.api.common.JobExecutionResult;
import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.datastream.DataStreamSource;
import com.lxb.flink.api.graph.StreamGraph;
import com.lxb.flink.api.graph.StreamGraphGenerator;
import com.lxb.flink.configuration.Configuration;
import com.lxb.flink.core.execution.DefaultExecutorServiceLoader;
import com.lxb.flink.core.execution.JobClient;
import com.lxb.flink.core.execution.JobListener;
import com.lxb.flink.core.execution.PipelineExecutorFactory;
import com.lxb.flink.core.execution.PipelineExecutorServiceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StreamExecutionEnvironment {
    public static final  String DEFAULT_JOB_NAME               = "Flink Streaming Job";
    private static final long   DEFAULT_NETWORK_BUFFER_TIMEOUT = 100L;

    private final ExecutionConfig config            = new ExecutionConfig();
    private       long            bufferTimeout     = DEFAULT_NETWORK_BUFFER_TIMEOUT;
    protected     boolean         isChainingEnabled = true;

    protected final List<Transformation<?>> transformations = new ArrayList<>();

    private final PipelineExecutorServiceLoader executorServiceLoader;

    private final Configuration configuration;

    private final ClassLoader userClassloader;

    private final List<JobListener> jobListeners = new ArrayList<>();

    public StreamExecutionEnvironment() {
        this(new Configuration());
    }

    public StreamExecutionEnvironment(final Configuration configuration) {
        this(configuration, null);
    }

    public StreamExecutionEnvironment(
            final Configuration configuration,
            final ClassLoader userClassloader) {
        this(new DefaultExecutorServiceLoader(), configuration, userClassloader);
    }

    public StreamExecutionEnvironment(
            final PipelineExecutorServiceLoader executorServiceLoader,
            final Configuration configuration,
            final ClassLoader userClassloader) {
        this.executorServiceLoader = executorServiceLoader;
        this.configuration = configuration;
        this.userClassloader = userClassloader == null ? getClass().getClassLoader() : userClassloader;
    }


    public static StreamExecutionEnvironment getExecutionEnvironment() {
        return null;
    }

    public StreamExecutionEnvironment setParallelism(int parallelism) {
        return this;
    }

    public final <OUT> DataStreamSource<OUT> fromElements(OUT... data) {
        return null;
    }

    public JobExecutionResult execute() throws Exception {
        return execute(DEFAULT_JOB_NAME);
    }

    public JobExecutionResult execute(String jobName) throws Exception {
        return execute(getStreamGraph(jobName));
    }

    public JobExecutionResult execute(StreamGraph streamGraph) throws Exception {
        final JobClient jobClient = executeAsync(streamGraph);

        try {
            final JobExecutionResult jobExecutionResult;

            jobExecutionResult = jobClient.getJobExecutionResult(userClassloader).get();

            jobListeners.forEach(jobListener -> jobListener.onJobExecuted(jobExecutionResult, null));

            return jobExecutionResult;
        } catch (Throwable t) {
            return null;
        }
    }

    public JobClient executeAsync(StreamGraph streamGraph) throws Exception {
        final PipelineExecutorFactory executorFactory =
                executorServiceLoader.getExecutorFactory(configuration);

        CompletableFuture<JobClient> jobClientFuture = executorFactory
                // 获取执行器：LocalExecutor
                .getExecutor(configuration)
                // 执行
                .execute(streamGraph, configuration);

        JobClient jobClient = jobClientFuture.get();
        jobListeners.forEach(jobListener -> jobListener.onJobSubmitted(jobClient, null));
        return jobClient;
    }

    public StreamGraph getStreamGraph(String jobName) {
        return getStreamGraphGenerator().setJobName(jobName).generate();
    }

    private StreamGraphGenerator getStreamGraphGenerator() {
        return new StreamGraphGenerator(transformations, config)
                .setChaining(isChainingEnabled)
                .setDefaultBufferTimeout(bufferTimeout);
    }


    public <F> F clean(F f) {
        return f;
    }

    public void addOperator(Transformation<?> transformation) {
        this.transformations.add(transformation);
    }

    public int getParallelism() {
        return config.getParallelism();
    }

}
