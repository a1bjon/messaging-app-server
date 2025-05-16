package com.xepr.core.dto.request;

import com.xepr.core.dto.Sendable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

import java.util.UUID;

@Getter
@Setter
@ToString
public final class DeleteMediaMessageRequestDTO implements Sendable {

    @NonNull
    private UUID mediaMessageId;
}
