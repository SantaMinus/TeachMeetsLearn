package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;

import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.exception.InvalidAuthException;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;
  private final StudentService studentService;
  private final TeacherService teacherService;

  @Override
  public UserDetails loadUserByUsername(String login) {
    User user = userRepository.findByLogin(login)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
    return new org.springframework.security.core.userdetails.User(user.getLogin(),
        user.getPassword(), List.of(authority));
  }

  @Observed(name = "auth.signup",
      contextualName = "signup",
      // test values below
      lowCardinalityKeyValues = {"userType", "userType2"})
  public void signUp(SignUpDto data) throws InvalidAuthException {
    if (data.role() == null) {
      throw new InvalidAuthException("Role is not specified");
    }
    log.info("Checking user login {}", data.login());
    if (userRepository.findByLogin(data.login()).isPresent()) {
      throw new InvalidAuthException("Username already exists");
    }
    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    User newUser = new User(data.login(), encryptedPassword, data.role().getValue());
    log.info("Saving a new user {}", data.login());
    userRepository.save(newUser);

    if (Objects.equals(data.role(), STUDENT)) {
      log.info("Creating Student info");
      studentService.create(data.name(), data.lastName(), newUser);
    } else {
      log.info("Creating Teacher info");
      teacherService.create(data.name(), data.lastName(), newUser);
    }
  }
}
