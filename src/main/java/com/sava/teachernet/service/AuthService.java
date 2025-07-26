package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;

import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.exception.InvalidAuthException;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;
  private final StudentService studentService;
  private final TeacherService teacherService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String login) {
    User user = userRepository.findByLogin(login)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
    return new org.springframework.security.core.userdetails.User(user.getLogin(),
        user.getPassword(), List.of(authority));
  }

  public void signUp(SignUpDto data) throws InvalidAuthException {
    if (data.role() == null) {
      throw new InvalidAuthException("Role is not specified");
    }
    if (userRepository.findByLogin(data.login()).isPresent()) {
      throw new InvalidAuthException("Username already exists");
    }
    String encryptedPassword = passwordEncoder.encode(data.password());
    User newUser = new User(data.login(), encryptedPassword, data.role().getValue());
    userRepository.save(newUser);

    if (Objects.equals(data.role(), STUDENT)) {
      studentService.create(data.name(), data.lastName(), newUser);
    } else {
      teacherService.create(data.name(), data.lastName(), newUser);
    }
  }

  /**
   * Refreshes the current authentication with updated authorities
   */
  public void refreshAuthentication() {
    Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

    if (currentAuth != null && currentAuth.isAuthenticated()) {
      String username = currentAuth.getName();
      UserDetails userDetails = loadUserByUsername(username);

      Authentication newAuth = new UsernamePasswordAuthenticationToken(
          userDetails,
          currentAuth.getCredentials(),
          userDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
  }
}
