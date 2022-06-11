package com.lxb.flink.types;

public class NullKeyFieldException extends RuntimeException {
    private final int fieldNumber;

    public NullKeyFieldException() {
        super();
        this.fieldNumber = -1;
    }

    public NullKeyFieldException(NullFieldException nfex) {
        super();
        this.fieldNumber = nfex.getFieldPos();
    }

    public NullKeyFieldException(String message) {
        super(message);
        this.fieldNumber = -1;
    }

    public NullKeyFieldException(int fieldNumber) {
        super("Field " + fieldNumber + " is null, but expected to hold a key.");
        this.fieldNumber = fieldNumber;
    }

    public int getFieldNumber() {
        return this.fieldNumber;
    }

}
