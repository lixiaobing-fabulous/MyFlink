package com.lxb.flink.api.dag;

import com.lxb.flink.api.common.functions.InvalidTypesException;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.java.typeutils.MissingTypeInfo;

public abstract class Transformation<T> {
    public static final int UPPER_BOUND_MAX_PARALLELISM = 1 << 15;

    public static final int DEFAULT_MANAGED_MEMORY_WEIGHT = 1;

    protected static Integer idCounter = 0;
    public static int getNewNodeId() {
        idCounter++;
        return idCounter;
    }
    protected int id;

    protected String name;

    protected TypeInformation<T> outputType;
    protected boolean typeUsed;

    private int parallelism;
    private int maxParallelism = -1;



}
