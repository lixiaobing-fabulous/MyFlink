package com.lxb.flink.types;

public class NullFieldException extends RuntimeException {
    private final int fieldPos;

    public NullFieldException() {
        super();
        this.fieldPos = -1;
    }

    public NullFieldException(String message) {
        super(message);
        this.fieldPos = -1;
    }

    public NullFieldException(int fieldIdx) {
        super("Field " + fieldIdx + " is null, but expected to hold a value.");
        this.fieldPos = fieldIdx;
    }

    public NullFieldException(int fieldIdx, Throwable cause) {
        super("Field " + fieldIdx + " is null, but expected to hold a value.", cause);
        this.fieldPos = fieldIdx;
    }

    public int getFieldPos() {
        return this.fieldPos;
    }

}
