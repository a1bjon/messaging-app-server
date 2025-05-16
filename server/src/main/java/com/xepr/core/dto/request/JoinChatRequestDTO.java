package com.xepr.core.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

import java.util.UUID;

@Getter
@Setter
@ToString
public final class JoinChatRequestDTO {

    @NonNull
    private Long id;

    @NonNull
    private UUID code;
}
