package com.lxb.flink.api.dag;

import com.lxb.flink.api.common.functions.InvalidTypesException;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.java.typeutils.MissingTypeInfo;

public abstract class Transformation<T> {
    protected final int    id;
    private         String coLocationGroupKey;

    protected        TypeInformation<T> outputType;
    private          int                parallelism;
    protected        String             name;
    protected        boolean            typeUsed;
    private          String             slotSharingGroup;
    protected static Integer            idCounter      = 0;
    private          int                maxParallelism = -1;

    public static int getNewNodeId() {
        idCounter++;
        return idCounter;
    }

    public Transformation(String name, TypeInformation<T> outputType, int parallelism) {
        this.id = getNewNodeId();
        this.name = name;
        this.outputType = outputType;
        this.parallelism = parallelism;
        this.slotSharingGroup = null;
    }

    public TypeInformation<T> getOutputType() {
        if (outputType instanceof MissingTypeInfo) {
            MissingTypeInfo typeInfo = (MissingTypeInfo) this.outputType;
            throw new InvalidTypesException(
                    "The return type of function '"
                            + typeInfo.getFunctionName()
                            + "' could not be determined automatically, due to type erasure. "
                            + "You can give type information hints by using the returns(...) "
                            + "method on the result of the transformation call, or by letting "
                            + "your function implement the 'ResultTypeQueryable' "
                            + "interface.", typeInfo.getTypeException());
        }
        typeUsed = true;
        return this.outputType;
    }

    public int getParallelism() {
        return parallelism;
    }

    public int getId() {
        return id;
    }

    public String getCoLocationGroupKey() {
        return coLocationGroupKey;
    }

    public String getName() {
        return name;
    }

    public boolean isTypeUsed() {
        return typeUsed;
    }

    public String getSlotSharingGroup() {
        return slotSharingGroup;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }


}
