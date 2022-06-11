package com.lxb.flink.api.common.accumulators;

import java.io.Serializable;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-06-10
 */
public class AccumulatorHelper {

    public static <V, A extends Serializable> void compareAccumulatorTypes(String name, Class<? extends Accumulator> aClass, Class<? extends Accumulator<V,A>> accumulatorClass) {
    }
}
