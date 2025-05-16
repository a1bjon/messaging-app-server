package com.xepr.core.repository;

import com.xepr.core.model.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = "SELECT name FROM chat_model WHERE name = ?1", nativeQuery = true)
    String findChatName(String name);
}
