package com.xepr.core.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

@Getter
@Setter
@ToString
public final class LeaveChatRequestDTO {

    @NonNull
    private Long id;
}
