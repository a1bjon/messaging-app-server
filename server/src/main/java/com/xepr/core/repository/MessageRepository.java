package com.xepr.core.repository;

import com.xepr.core.model.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface MessageRepository<T extends Message> extends JpaRepository<T, UUID> {
}
