package com.lxb.flink.api.graph;

import com.lxb.flink.api.TimeCharacteristic;
import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.dag.Pipeline;
import com.lxb.flink.api.java.functions.KeySelector;
import com.lxb.flink.api.java.tupple.Tuple2;
import com.lxb.flink.api.java.tupple.Tuple3;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.transformations.ShuffleMode;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.java.typeutils.MissingTypeInfo;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.runtime.jobgraph.ScheduleMode;
import com.lxb.flink.runtime.jobgraph.tasks.AbstractInvokable;
import com.lxb.flink.runtime.partitioner.ForwardPartitioner;
import com.lxb.flink.runtime.partitioner.RebalancePartitioner;
import com.lxb.flink.runtime.partitioner.StreamPartitioner;
import com.lxb.flink.runtime.state.StateBackend;
import com.lxb.flink.runtime.tasks.OneInputStreamTask;
import com.lxb.flink.runtime.tasks.SourceStreamTask;
import com.lxb.flink.utl.OutputTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StreamGraph implements Pipeline {
    private String                 jobName;
    private ScheduleMode           scheduleMode;
    private boolean                chaining;
    private TimeCharacteristic     timeCharacteristic;
    private GlobalDataExchangeMode globalDataExchangeMode;
    private StateBackend           stateBackend;

    private       Map<Integer, StreamNode>                                         streamNodes;
    private final ExecutionConfig                                                  executionConfig;
    private       Map<Integer, Tuple2<Integer, List<String>>>                      virtualSelectNodes;
    private       Map<Integer, Tuple2<Integer, OutputTag>>                         virtualSideOutputNodes;
    private       Map<Integer, Tuple3<Integer, StreamPartitioner<?>, ShuffleMode>> virtualPartitionNodes;
    protected     Map<Integer, String>                                             vertexIDtoBrokerID;
    protected     Map<Integer, Long>                                               vertexIDtoLoopTimeout;
    private       Set<Tuple2<StreamNode, StreamNode>>                              iterationSourceSinkPairs;
    private       Set<Integer>                                                     sources;
    private       Set<Integer>                                                     sinks;

    public StreamGraph(ExecutionConfig executionConfig) {
        this.executionConfig = executionConfig;
        clear();
    }

    public void clear() {
        streamNodes = new HashMap<>();
        virtualSelectNodes = new HashMap<>();
        virtualSideOutputNodes = new HashMap<>();
        virtualPartitionNodes = new HashMap<>();
        vertexIDtoBrokerID = new HashMap<>();
        vertexIDtoLoopTimeout = new HashMap<>();
        iterationSourceSinkPairs = new HashSet<>();
        sources = new HashSet<>();
        sinks = new HashSet<>();
    }

    public void addEdge(Integer upStreamVertexID, Integer downStreamVertexID, int typeNumber) {
        addEdgeInternal(upStreamVertexID,
                downStreamVertexID,
                typeNumber,
                null,
                new ArrayList<String>(),
                null,
                null);

    }

    private void addEdgeInternal(Integer upStreamVertexID,
                                 Integer downStreamVertexID,
                                 int typeNumber,
                                 StreamPartitioner<?> partitioner,
                                 List<String> outputNames,
                                 OutputTag outputTag,
                                 ShuffleMode shuffleMode) {
        if (virtualPartitionNodes.containsKey(upStreamVertexID)) {
            int virtualId = upStreamVertexID;
            upStreamVertexID = virtualPartitionNodes.get(virtualId).f0;
            if (partitioner == null) {
                partitioner = virtualPartitionNodes.get(virtualId).f1;
            }
            shuffleMode = virtualPartitionNodes.get(virtualId).f2;
            addEdgeInternal(upStreamVertexID, downStreamVertexID, typeNumber, partitioner, outputNames, outputTag, shuffleMode);
        } else {
            StreamNode upstreamNode   = getStreamNode(upStreamVertexID);
            StreamNode downstreamNode = getStreamNode(downStreamVertexID);
            if (partitioner == null && upstreamNode.getParallelism() == downstreamNode.getParallelism()) {
                partitioner = new ForwardPartitioner<>();
            } else if (partitioner == null) {
                partitioner = new RebalancePartitioner<>();
            }

            if (shuffleMode == null) {
                shuffleMode = ShuffleMode.UNDEFINED;
            }

            StreamEdge edge = new StreamEdge(upstreamNode, downstreamNode, typeNumber, outputNames, partitioner, outputTag, shuffleMode);
            getStreamNode(edge.getSourceId()).addOutEdge(edge);
            getStreamNode(edge.getTargetId()).addInEdge(edge);
        }
    }

    public <IN, OUT> void addLegacySource(
            Integer vertexID,
            String slotSharingGroup,
            String coLocationGroup,
            StreamOperatorFactory<OUT> operatorFactory,
            TypeInformation<IN> inTypeInfo,
            TypeInformation<OUT> outTypeInfo,
            String operatorName) {
        addOperator(vertexID, slotSharingGroup, coLocationGroup, operatorFactory, inTypeInfo, outTypeInfo, operatorName);
        // graph
        // vertex: 点
        // edge: 边
        System.out.println("vertexID: " + vertexID);
        sources.add(vertexID);
    }

    public <IN, OUT> void addSink(
            Integer vertexID,
            String slotSharingGroup,
            String coLocationGroup,
            StreamOperatorFactory<OUT> operatorFactory,
            TypeInformation<IN> inTypeInfo,
            TypeInformation<OUT> outTypeInfo,
            String operatorName) {
        addOperator(vertexID, slotSharingGroup, coLocationGroup, operatorFactory, inTypeInfo, outTypeInfo, operatorName);
        sinks.add(vertexID);
    }


    public <IN, OUT> void addOperator(
            Integer vertexID,
            String slotSharingGroup,
            String coLocationGroup,
            StreamOperatorFactory<OUT> operatorFactory,
            TypeInformation<IN> inTypeInfo,
            TypeInformation<OUT> outTypeInfo,
            String operatorName) {
        Class<? extends AbstractInvokable> invokableClass =
                operatorFactory.isStreamSource() ? SourceStreamTask.class : OneInputStreamTask.class;
        addOperator(vertexID, slotSharingGroup, coLocationGroup, operatorFactory, inTypeInfo,
                outTypeInfo, operatorName, invokableClass);
    }

    private <IN, OUT> void addOperator(
            Integer vertexID,
            String slotSharingGroup,
            String coLocationGroup,
            StreamOperatorFactory<OUT> operatorFactory,
            TypeInformation<IN> inTypeInfo,
            TypeInformation<OUT> outTypeInfo,
            String operatorName,
            Class<? extends AbstractInvokable> invokableClass) {

        addNode(vertexID, slotSharingGroup, coLocationGroup, invokableClass, operatorFactory, operatorName);
        setSerializers(vertexID, createSerializer(inTypeInfo), null, createSerializer(outTypeInfo));
        if (operatorFactory.isOutputTypeConfigurable() && outTypeInfo != null) {
            // sets the output type which must be know at StreamGraph creation time
            operatorFactory.setOutputType(outTypeInfo, executionConfig);
        }
    }


    protected StreamNode addNode(
            Integer vertexID,
            String slotSharingGroup,
            String coLocationGroup,
            Class<? extends AbstractInvokable> vertexClass,
            StreamOperatorFactory<?> operatorFactory,
            String operatorName) {

        if (streamNodes.containsKey(vertexID)) {
            throw new RuntimeException("Duplicate vertexID " + vertexID);
        }

        StreamNode vertex = new StreamNode(
                vertexID,
                slotSharingGroup,
                coLocationGroup,
                operatorFactory,
                operatorName,
                new ArrayList<>(),
                vertexClass);

        streamNodes.put(vertexID, vertex);

        return vertex;
    }

    public void addVirtualPartitionNode(
            Integer originalId,
            Integer virtualId,
            StreamPartitioner<?> partitioner,
            ShuffleMode shuffleMode) {

        if (virtualPartitionNodes.containsKey(virtualId)) {
            throw new IllegalStateException("Already has virtual partition node with id " + virtualId);
        }

        virtualPartitionNodes.put(virtualId, new Tuple3<>(originalId, partitioner, shuffleMode));
    }


    private <T> TypeSerializer<T> createSerializer(TypeInformation<T> typeInfo) {
        return typeInfo != null && !(typeInfo instanceof MissingTypeInfo) ?
                typeInfo.createSerializer(executionConfig) : null;
    }

    public void setSerializers(Integer vertexID, TypeSerializer<?> in1, TypeSerializer<?> in2, TypeSerializer<?> out) {
        StreamNode vertex = getStreamNode(vertexID);
        vertex.setSerializersIn(in1, in2);
        vertex.setSerializerOut(out);
    }

    public void setOneInputStateKey(Integer vertexID, KeySelector<?, ?> keySelector, TypeSerializer<?> keySerializer) {
        StreamNode node = getStreamNode(vertexID);
        node.setStatePartitioners(keySelector);
        node.setStateKeySerializer(keySerializer);
    }

    public StreamNode getStreamNode(Integer vertexID) {
        return streamNodes.get(vertexID);
    }

    public void setParallelism(Integer vertexID, int parallelism) {
        if (getStreamNode(vertexID) != null) {
            getStreamNode(vertexID).setParallelism(parallelism);
        }
    }

    public void setMaxParallelism(int vertexID, int maxParallelism) {
        if (getStreamNode(vertexID) != null) {
            getStreamNode(vertexID).setMaxParallelism(maxParallelism);
        }
    }


    public ExecutionConfig getExecutionConfig() {
        return executionConfig;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setChaining(boolean chaining) {
        this.chaining = chaining;
    }

    public void setStateBackend(StateBackend backend) {
        this.stateBackend = backend;
    }

    public StateBackend getStateBackend() {
        return this.stateBackend;
    }

    public ScheduleMode getScheduleMode() {
        return scheduleMode;
    }

    public void setScheduleMode(ScheduleMode scheduleMode) {
        this.scheduleMode = scheduleMode;
    }

    public TimeCharacteristic getTimeCharacteristic() {
        return timeCharacteristic;
    }

    public void setTimeCharacteristic(TimeCharacteristic timeCharacteristic) {
        this.timeCharacteristic = timeCharacteristic;
    }

    public GlobalDataExchangeMode getGlobalDataExchangeMode() {
        return globalDataExchangeMode;
    }

    public void setGlobalDataExchangeMode(GlobalDataExchangeMode globalDataExchangeMode) {
        this.globalDataExchangeMode = globalDataExchangeMode;
    }

    public boolean isChainingEnabled() {
        return chaining;
    }

}
