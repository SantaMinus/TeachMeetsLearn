package com.sava.teachernet.service;

import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.exception.InvalidJwtException;
import com.sava.teachernet.model.UserEntity;
import com.sava.teachernet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

  @Autowired
  UserRepository repository;

  @Override
  public UserDetails loadUserByUsername(String username) {
    return repository.findByLogin(username);
  }

  public UserDetails signUp(SignUpDto data) throws InvalidJwtException {
    if (repository.findByLogin(data.login()) != null) {
      throw new InvalidJwtException("Username already exists");
    }
    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    UserEntity newUser = new UserEntity(data.login(), encryptedPassword, data.role());
    return repository.save(newUser);
  }
}
