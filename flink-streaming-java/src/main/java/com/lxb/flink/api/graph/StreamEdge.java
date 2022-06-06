package com.lxb.flink.api.graph;

import com.lxb.flink.api.transformations.ShuffleMode;
import com.lxb.flink.runtime.partitioner.StreamPartitioner;
import com.lxb.flink.utl.OutputTag;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class StreamEdge implements Serializable {

    private final String edgeId;

    private final int sourceId;
    private final int targetId;

    private final int typeNumber;

    private final List<String> selectedNames;

    private final OutputTag outputTag;

    private StreamPartitioner<?> outputPartitioner;

    /**
     * The name of the operator in the source vertex.
     */
    private final String sourceOperatorName;

    /**
     * The name of the operator in the target vertex.
     */
    private final String targetOperatorName;

    private final ShuffleMode shuffleMode;

    public StreamEdge(StreamNode sourceVertex, StreamNode targetVertex, int typeNumber,
                      List<String> selectedNames, StreamPartitioner<?> outputPartitioner, OutputTag outputTag) {
        this(sourceVertex,
                targetVertex,
                typeNumber,
                selectedNames,
                outputPartitioner,
                outputTag,
                ShuffleMode.UNDEFINED);
    }

    public StreamEdge(StreamNode sourceVertex, StreamNode targetVertex, int typeNumber,
                      List<String> selectedNames, StreamPartitioner<?> outputPartitioner, OutputTag outputTag,
                      ShuffleMode shuffleMode) {
        this.sourceId = sourceVertex.getId();
        this.targetId = targetVertex.getId();
        this.typeNumber = typeNumber;
        this.selectedNames = selectedNames;
        this.outputPartitioner = outputPartitioner;
        this.outputTag = outputTag;
        this.sourceOperatorName = sourceVertex.getOperatorName();
        this.targetOperatorName = targetVertex.getOperatorName();
        this.shuffleMode = shuffleMode;

        this.edgeId = sourceVertex + "_" + targetVertex + "_" + typeNumber + "_" + selectedNames
                + "_" + outputPartitioner;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    public List<String> getSelectedNames() {
        return selectedNames;
    }

    public OutputTag getOutputTag() {
        return this.outputTag;
    }

    public StreamPartitioner<?> getPartitioner() {
        return outputPartitioner;
    }

    public ShuffleMode getShuffleMode() {
        return shuffleMode;
    }

    public void setPartitioner(StreamPartitioner<?> partitioner) {
        this.outputPartitioner = partitioner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeId, outputTag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StreamEdge that = (StreamEdge) o;
        return Objects.equals(edgeId, that.edgeId) &&
                Objects.equals(outputTag, that.outputTag);
    }

    @Override
    public String toString() {
        return "(" + (sourceOperatorName + "-" + sourceId) + " -> " + (targetOperatorName + "-" + targetId)
                + ", typeNumber=" + typeNumber + ", selectedNames=" + selectedNames + ", outputPartitioner=" + outputPartitioner
                + ", outputTag=" + outputTag + ')';
    }
}
