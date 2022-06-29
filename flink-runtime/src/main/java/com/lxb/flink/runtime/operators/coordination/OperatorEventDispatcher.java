package com.lxb.flink.runtime.operators.coordination;


import com.lxb.flink.runtime.jobgraph.OperatorID;

public interface OperatorEventDispatcher {
	void registerEventHandler(OperatorID operator, OperatorEventHandler handler);
}
