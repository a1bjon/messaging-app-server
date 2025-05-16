package com.xepr.core.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

import java.util.UUID;

@Getter
@Setter
@ToString
public final class EditTextMessageResponseDTO {

    @NonNull
    private UUID id;

    @NonNull
    private String text;
}
