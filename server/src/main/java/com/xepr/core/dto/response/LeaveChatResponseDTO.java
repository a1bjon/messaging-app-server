package com.xepr.core.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

import java.util.List;

@Getter
@Setter
@ToString
public final class LeaveChatResponseDTO {

    @NonNull
    private Long id;

    @NonNull
    private List<Long> users;
}
