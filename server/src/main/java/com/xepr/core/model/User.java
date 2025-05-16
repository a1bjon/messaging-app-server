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

@Entity
@Table(name = "user_model")
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> chats = new LinkedList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> trustedIps = new LinkedList<>();

    private Boolean authenticated;

    private Integer authenticationCode;
}
