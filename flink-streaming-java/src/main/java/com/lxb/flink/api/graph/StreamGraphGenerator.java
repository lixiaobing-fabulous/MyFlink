package com.lxb.flink.api.graph;

import com.lxb.flink.api.TimeCharacteristic;
import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.dag.Transformation;
import com.lxb.flink.api.transformations.LegacySourceTransformation;
import com.lxb.flink.api.transformations.OneInputTransformation;
import com.lxb.flink.api.transformations.PartitionTransformation;
import com.lxb.flink.api.transformations.SinkTransformation;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.runtime.jobgraph.ScheduleMode;
import com.lxb.flink.runtime.state.StateBackend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamGraphGenerator {
    public static final String DEFAULT_JOB_NAME               = "Flink Streaming Job";
    public static final long   DEFAULT_NETWORK_BUFFER_TIMEOUT = 100L;

    public static final ScheduleMode            DEFAULT_SCHEDULE_MODE       = ScheduleMode.EAGER;
    public static final TimeCharacteristic      DEFAULT_TIME_CHARACTERISTIC = TimeCharacteristic.ProcessingTime;
    private             String                  jobName                     = DEFAULT_JOB_NAME;
    private             StreamGraph             streamGraph;
    private final       ExecutionConfig         executionConfig;
    private final       List<Transformation<?>> transformations;
    private             StateBackend            stateBackend;
    private             boolean                 chaining                    = true;
    private             long                    defaultBufferTimeout        = DEFAULT_NETWORK_BUFFER_TIMEOUT;

    private ScheduleMode                                scheduleMode           = DEFAULT_SCHEDULE_MODE;
    private TimeCharacteristic                          timeCharacteristic     = DEFAULT_TIME_CHARACTERISTIC;
    private GlobalDataExchangeMode                      globalDataExchangeMode = GlobalDataExchangeMode.ALL_EDGES_PIPELINED;
    private Map<Transformation<?>, Collection<Integer>> alreadyTransformed;

    public StreamGraphGenerator(List<Transformation<?>> transformations, ExecutionConfig executionConfig) {
        this.transformations = transformations;
        this.executionConfig = executionConfig;
    }


    public StreamGraphGenerator setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public StreamGraph generate() {
        streamGraph = new StreamGraph(executionConfig);
        streamGraph.setStateBackend(stateBackend);
        streamGraph.setChaining(chaining);
        streamGraph.setScheduleMode(scheduleMode);
        streamGraph.setTimeCharacteristic(timeCharacteristic);
        streamGraph.setJobName(jobName);
        streamGraph.setGlobalDataExchangeMode(globalDataExchangeMode);

        alreadyTransformed = new HashMap<>();

        for (Transformation<?> transformation : transformations) {
            transform(transformation);
        }

        final StreamGraph builtStreamGraph = streamGraph;

        alreadyTransformed.clear();
        alreadyTransformed = null;
        streamGraph = null;

        return builtStreamGraph;
    }

    private Collection<Integer> transform(Transformation<?> transform) {
        if (alreadyTransformed.containsKey(transform)) {
            return alreadyTransformed.get(transform);
        }
        Collection<Integer> transformedIds;
        if (transform instanceof OneInputTransformation<?, ?>) {
            transformedIds = transformOneInputTransform((OneInputTransformation<?, ?>) transform);
        } else if (transform instanceof LegacySourceTransformation<?>) {
            transformedIds = transformLegacySource((LegacySourceTransformation<?>) transform);
        } else if (transform instanceof SinkTransformation<?>) {
            transformedIds = transformSink((SinkTransformation<?>) transform);
        } else if (transform instanceof PartitionTransformation<?>) {
            transformedIds = transformPartition((PartitionTransformation<?>) transform);
        } else {
            throw new IllegalStateException("Unknown transformation: " + transform);
        }

        if (!alreadyTransformed.containsKey(transform)) {
            alreadyTransformed.put(transform, transformedIds);
        }

        return transformedIds;
    }

    private <IN, OUT> Collection<Integer> transformOneInputTransform(OneInputTransformation<IN, OUT> transform) {

        Collection<Integer> inputIds = transform(transform.getInput());

        // the recursive call might have already transformed this
        if (alreadyTransformed.containsKey(transform)) {
            return alreadyTransformed.get(transform);
        }

        String slotSharingGroup = "default";

        streamGraph.addOperator(transform.getId(),
                slotSharingGroup,
                transform.getCoLocationGroupKey(),
                transform.getOperatorFactory(),
                transform.getInputType(),
                transform.getOutputType(),
                transform.getName());

        if (transform.getStateKeySelector() != null) {
            TypeSerializer<?> keySerializer = transform.getStateKeyType().createSerializer(executionConfig);
            streamGraph.setOneInputStateKey(transform.getId(), transform.getStateKeySelector(), keySerializer);
        }

        int parallelism = transform.getParallelism() != ExecutionConfig.PARALLELISM_DEFAULT ?
                transform.getParallelism() : executionConfig.getParallelism();
        streamGraph.setParallelism(transform.getId(), parallelism);
        streamGraph.setMaxParallelism(transform.getId(), transform.getMaxParallelism());

        for (Integer inputId : inputIds) {
            streamGraph.addEdge(inputId, transform.getId(), 0);
        }

        return Collections.singleton(transform.getId());
    }

    private <T> Collection<Integer> transformLegacySource(LegacySourceTransformation<T> source) {
        String slotSharingGroup = "default";

        streamGraph.addLegacySource(source.getId(),
                slotSharingGroup,
                source.getCoLocationGroupKey(),
                source.getOperatorFactory(),
                null,
                source.getOutputType(),
                "Source: " + source.getName());
        int parallelism = source.getParallelism() != ExecutionConfig.PARALLELISM_DEFAULT ?
                source.getParallelism() : executionConfig.getParallelism();
        streamGraph.setParallelism(source.getId(), parallelism);
        streamGraph.setMaxParallelism(source.getId(), source.getMaxParallelism());
        return Collections.singleton(source.getId());
    }

    private <T> Collection<Integer> transformSink(SinkTransformation<T> sink) {

        Collection<Integer> inputIds = transform(sink.getInput());

        // 将任务槽放在一个组里面，任务槽可以分组。
        String slotSharingGroup = "default";

        streamGraph.addSink(sink.getId(),
                slotSharingGroup,
                sink.getCoLocationGroupKey(),
                sink.getOperatorFactory(),
                sink.getInput().getOutputType(),
                null,
                "Sink: " + sink.getName());

        int parallelism = sink.getParallelism() != ExecutionConfig.PARALLELISM_DEFAULT ?
                sink.getParallelism() : executionConfig.getParallelism();
        streamGraph.setParallelism(sink.getId(), parallelism);
        streamGraph.setMaxParallelism(sink.getId(), sink.getMaxParallelism());

        for (Integer inputId : inputIds) {
            streamGraph.addEdge(inputId,
                    sink.getId(),
                    0
            );
        }

        return Collections.emptyList();
    }

    private <T> Collection<Integer> transformPartition(PartitionTransformation<T> partition) {
        Transformation<T> input     = partition.getInput();
        List<Integer>     resultIds = new ArrayList<>();

        Collection<Integer> transformedIds = transform(input);
        for (Integer transformedId : transformedIds) {
            int virtualId = Transformation.getNewNodeId();
            streamGraph.addVirtualPartitionNode(
                    transformedId, virtualId, partition.getPartitioner(), partition.getShuffleMode());
            resultIds.add(virtualId);
        }

        return resultIds;
    }

    public StreamGraphGenerator setChaining(boolean chaining) {
        this.chaining = chaining;
        return this;
    }

    public StreamGraphGenerator setDefaultBufferTimeout(long defaultBufferTimeout) {
        this.defaultBufferTimeout = defaultBufferTimeout;
        return this;
    }

}
