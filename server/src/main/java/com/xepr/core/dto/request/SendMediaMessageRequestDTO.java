package com.xepr.core.dto.request;

import com.xepr.core.dto.Sendable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

@Getter
@Setter
@ToString
public final class SendMediaMessageRequestDTO implements Sendable {

    @NonNull
    private Long senderId;

    private byte[] media;
}
