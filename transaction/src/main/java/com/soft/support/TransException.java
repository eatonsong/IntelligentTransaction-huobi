package com.soft.support;

public class TransException extends RuntimeException {

    public TransException(String message) {
        super(message);
    }

    public TransException(Throwable cause) {
        super(cause);
    }
}
