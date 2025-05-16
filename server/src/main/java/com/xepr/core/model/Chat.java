package com.xepr.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.LinkedList;
import java.util.UUID;

@Entity
@Table(name = "chat_model")
@Getter
@Setter
@ToString
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID code = UUID.randomUUID();

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> users = new LinkedList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UUID> messages = new LinkedList<>();
}
