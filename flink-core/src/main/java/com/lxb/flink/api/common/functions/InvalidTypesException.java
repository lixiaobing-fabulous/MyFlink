package com.lxb.flink.api.common.functions;

import com.lxb.flink.api.common.typeuitils.InvalidProgramException;

public class InvalidTypesException extends InvalidProgramException {

    public InvalidTypesException() {
        super();
    }

    public InvalidTypesException(String message) {
        super(message);
    }

    public InvalidTypesException(String message, Throwable e) {
        super(message, e);
    }
}
