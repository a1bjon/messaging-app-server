package com.xepr.core.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

@Getter
@Setter
@ToString
public final class AuthCodeRequestDTO {

    @NonNull
    private Long id;

    @NonNull
    private Integer code;
}
