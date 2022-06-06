package com.lxb.flink.api.graph;

public enum GlobalDataExchangeMode {
    ALL_EDGES_BLOCKING,
    FORWARD_EDGES_PIPELINED,
    POINTWISE_EDGES_PIPELINED,
    ALL_EDGES_PIPELINED

}
