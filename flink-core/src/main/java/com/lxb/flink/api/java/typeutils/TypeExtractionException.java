package com.lxb.flink.api.java.typeutils;

public class TypeExtractionException extends Exception {


    public TypeExtractionException(String message) {
        super(message);
    }

    public TypeExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeExtractionException(Throwable cause) {
        super(cause);
    }

    protected TypeExtractionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
