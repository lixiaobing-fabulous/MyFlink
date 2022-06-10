package com.lxb.flink.api.common.accumulators;

import java.io.Serializable;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public interface SimpleAccumulator<T extends Serializable> extends Accumulator<T, T> {
}
