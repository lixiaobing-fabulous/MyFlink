package com.lxb.flink.api.java.typeutils;

import com.lxb.flink.api.common.ExecutionConfig;
import com.lxb.flink.api.common.functions.InvalidTypesException;
import com.lxb.flink.api.common.typeinfo.TypeInformation;
import com.lxb.flink.api.common.typeuitils.TypeSerializer;

public class MissingTypeInfo extends TypeInformation<InvalidTypesException> {
    private final String                functionName;
    private final InvalidTypesException typeException;

    public MissingTypeInfo(String functionName) {
        this(functionName, new InvalidTypesException("An unknown error occured."));
    }

    public MissingTypeInfo(String functionName, InvalidTypesException typeException) {
        this.functionName = functionName;
        this.typeException = typeException;
    }

    @Override
    public TypeSerializer<InvalidTypesException> createSerializer(ExecutionConfig config) {
        throw new UnsupportedOperationException("The missing type information cannot be used as a type information.");
    }

    public String getFunctionName() {
        return functionName;
    }

    public InvalidTypesException getTypeException() {
        return typeException;
    }

}
