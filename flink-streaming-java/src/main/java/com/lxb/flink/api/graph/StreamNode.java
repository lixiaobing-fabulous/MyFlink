package com.lxb.flink.api.graph;

import com.lxb.flink.api.collector.selector.OutputSelector;
import com.lxb.flink.api.java.functions.KeySelector;
import com.lxb.flink.api.operators.StreamOperatorFactory;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;
import com.lxb.flink.runtime.jobgraph.tasks.AbstractInvokable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StreamNode implements Serializable {

    private final     int                                id;
    private final     String                             operatorName;
    private transient StreamOperatorFactory<?>           operatorFactory;
    private           String                             slotSharingGroup;
    private           String                             coLocationGroup;
    private final     Class<? extends AbstractInvokable> jobVertexClass;
    private           List<OutputSelector<?>>            outputSelectors;

    private KeySelector<?, ?>[] statePartitioners = new KeySelector[0];

    private TypeSerializer<?>[] typeSerializersIn = new TypeSerializer[0];
    private TypeSerializer<?>   typeSerializerOut;
    private TypeSerializer<?>   stateKeySerializer;
    private int                 maxParallelism;
    private int                 parallelism;
    private List<StreamEdge>    inEdges           = new ArrayList<>();
    private List<StreamEdge>    outEdges          = new ArrayList<>();

    public StreamNode(
            Integer id,
            String slotSharingGroup,
            String coLocationGroup,
            StreamOperatorFactory<?> operatorFactory,
            String operatorName,
            List<OutputSelector<?>> outputSelector,
            Class<? extends AbstractInvokable> jobVertexClass) {
        this.id = id;
        this.operatorName = operatorName;
        this.operatorFactory = operatorFactory;
        this.outputSelectors = outputSelector;
        this.jobVertexClass = jobVertexClass;
        this.slotSharingGroup = slotSharingGroup;
        this.coLocationGroup = coLocationGroup;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    void setMaxParallelism(int maxParallelism) {
        this.maxParallelism = maxParallelism;
    }

    int getMaxParallelism() {
        return maxParallelism;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setStatePartitioners(KeySelector<?, ?>... statePartitioners) {
        this.statePartitioners = statePartitioners;
    }

    public void setStateKeySerializer(TypeSerializer<?> stateKeySerializer) {
        this.stateKeySerializer = stateKeySerializer;
    }


    public void setSerializersIn(TypeSerializer<?>... typeSerializersIn) {
        this.typeSerializersIn = typeSerializersIn;
    }

    public TypeSerializer<?>[] getTypeSerializersIn() {
        return typeSerializersIn;
    }

    public TypeSerializer<?> getTypeSerializerIn(int index) {
        return typeSerializersIn[index];
    }

    public TypeSerializer<?> getTypeSerializerOut() {
        return typeSerializerOut;
    }

    public void setSerializerOut(TypeSerializer<?> typeSerializerOut) {
        this.typeSerializerOut = typeSerializerOut;
    }

    public int getId() {
        return id;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void addOutEdge(StreamEdge outEdge) {
        if (outEdge.getSourceId() != getId()) {
            throw new IllegalArgumentException("Source id doesn't match the StreamNode id");
        } else {
            outEdges.add(outEdge);
        }
    }

    public void addInEdge(StreamEdge inEdge) {
        if (inEdge.getTargetId() != getId()) {
            throw new IllegalArgumentException("Destination id doesn't match the StreamNode id");
        } else {
            inEdges.add(inEdge);
        }
    }

}
