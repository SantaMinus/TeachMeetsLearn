package com.sava.teachernet.repository;

import com.sava.teachernet.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  UserDetails findByLogin(String login);
}