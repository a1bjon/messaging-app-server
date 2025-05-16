package com.xepr.core.dto.response;

import com.xepr.core.dto.Sendable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class SendMediaMessageResponseDTO implements Sendable {

    @NonNull
    private Long id;

    @NonNull
    private Long senderId;

    @NonNull
    private String timestamp;

    private byte[] media;
}
