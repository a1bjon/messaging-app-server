package com.xepr.core.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

import java.util.List;

@Getter
@Setter
@ToString
public final class CreateChatRequestDTO {

    @NonNull
    private String name;

    @NonNull
    private List<Long> users;
}
