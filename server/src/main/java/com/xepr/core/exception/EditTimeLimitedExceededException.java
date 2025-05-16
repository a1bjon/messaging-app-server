package com.xepr.core.exception;

public class EditTimeLimitedExceededException extends Exception {

    public EditTimeLimitedExceededException() {
    }

    public EditTimeLimitedExceededException(String message) {
        super(message);
    }
}