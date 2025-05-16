package com.xepr.core.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

@Getter
@Setter
@ToString
public final class UpdateChatResponseDTO {

    @NonNull
    private Long id;

    @NonNull
    private String name;
}
