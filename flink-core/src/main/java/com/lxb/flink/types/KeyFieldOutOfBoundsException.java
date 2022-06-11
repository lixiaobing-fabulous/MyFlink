package com.lxb.flink.types;

public class KeyFieldOutOfBoundsException extends RuntimeException {
    private final int fieldNumber;

    public KeyFieldOutOfBoundsException() {
        super();
        this.fieldNumber = -1;
    }

    public KeyFieldOutOfBoundsException(String message) {
        super(message);
        this.fieldNumber = -1;
    }

    public KeyFieldOutOfBoundsException(int fieldNumber) {
        super("Field " + fieldNumber + " is accessed for a key, but out of bounds in the record.");
        this.fieldNumber = fieldNumber;
    }

    public KeyFieldOutOfBoundsException(int fieldNumber, Throwable parent) {
        super("Field " + fieldNumber + " is accessed for a key, but out of bounds in the record.", parent);
        this.fieldNumber = fieldNumber;
    }

    public int getFieldNumber() {
        return this.fieldNumber;
    }

}
