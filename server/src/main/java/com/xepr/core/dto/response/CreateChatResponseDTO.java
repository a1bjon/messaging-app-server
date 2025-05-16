package com.xepr.core.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

import java.util.UUID;
import java.util.List;

@Getter
@Setter
@ToString
public final class CreateChatResponseDTO {

    @NonNull
    private Long id;

    @NonNull
    private UUID code;

    @NonNull
    private String name;

    @NonNull
    private List<Long> users;
}
