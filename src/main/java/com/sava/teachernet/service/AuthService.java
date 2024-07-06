package com.sava.teachernet.service;

import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.exception.InvalidAuthException;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

  @Autowired
  UserRepository repository;

  @Override
  public UserDetails loadUserByUsername(String login) {
    User user = repository.findByLogin(login)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
    return new org.springframework.security.core.userdetails.User(user.getLogin(),
        user.getPassword(), List.of(authority));
  }

  public void signUp(SignUpDto data) throws InvalidAuthException {
    if (repository.findByLogin(data.login()).isPresent()) {
      throw new InvalidAuthException("Username already exists");
    }
    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    String role = switch (data.role()) {
      case STUDENT -> "ROLE_STUDENT";
      case TEACHER -> "ROLE_TEACHER";
    };
    User newUser = new User(data.login(), encryptedPassword, role);

    repository.save(newUser);
  }
}
