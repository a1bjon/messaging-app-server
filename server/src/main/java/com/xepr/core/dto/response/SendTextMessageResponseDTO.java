package com.xepr.core.dto.response;

import com.xepr.core.dto.Sendable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class SendTextMessageResponseDTO implements Sendable {

    @NonNull
    private Long id;

    @NonNull
    private Long senderId;

    @NonNull
    private String timestamp;

    @NonNull
    private String text;
}
