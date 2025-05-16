package com.xepr.core.response;

import lombok.Getter;

@Getter
public enum Status {

    SUCCESS("success"),
    ERROR("error");

    private final String status;

    Status(String status) {
        this.status = status;
    }
}
