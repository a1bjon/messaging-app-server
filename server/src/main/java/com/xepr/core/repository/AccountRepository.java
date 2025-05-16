package com.xepr.core.repository;

import com.xepr.core.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT email FROM user_model WHERE email = ?1", nativeQuery = true)
    String findUserEmail(String email);

    @Query(value = "SELECT * FROM user_model WHERE email = ?1", nativeQuery = true)
    Optional<User> findUserByEmail(String email);
}
