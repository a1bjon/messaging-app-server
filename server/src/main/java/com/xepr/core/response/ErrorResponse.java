package com.xepr.core.response;

public class ErrorResponse extends Response {

    public ErrorResponse(Status status, String message) {
        super(status, message);
    }
}
