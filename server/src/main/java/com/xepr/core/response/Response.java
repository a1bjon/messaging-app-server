package com.xepr.core.response;

import lombok.Getter;

@Getter
public abstract class Response {

    private final String status;

    private final String message;

    public Response(Status status, String message) {
        this.status = status.getStatus();
        this.message = message;
    }
}
