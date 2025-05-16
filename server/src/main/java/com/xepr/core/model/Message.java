package com.xepr.core.model;

import com.xepr.core.util.DateUtil;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long senderId;

    private String timestamp = DateUtil.getCurrentDateTime();
}
