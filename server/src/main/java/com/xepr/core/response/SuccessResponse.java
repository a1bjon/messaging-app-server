package com.xepr.core.response;

import com.xepr.core.util.DateUtil;

import lombok.Getter;

@Getter
public class SuccessResponse<T> extends Response {

    private final String timestamp = DateUtil.getCurrentDateTime();

    private T data;

    public SuccessResponse(Status status, String message, T data) {
        super(status, message);
        this.data = data;
    }

    public SuccessResponse(Status status, String message) {
        super(status, message);
    }
}
