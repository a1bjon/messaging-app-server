package com.xepr.core.repository;

import com.xepr.core.model.TextMessage;

import org.springframework.stereotype.Repository;

@Repository
public interface TextMessageRepository extends MessageRepository<TextMessage> {
}
