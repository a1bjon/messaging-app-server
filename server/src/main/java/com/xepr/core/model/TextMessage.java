package com.xepr.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "text_message_model")
@Getter
@Setter
@ToString
public class TextMessage extends Message {

    private String text;
}
