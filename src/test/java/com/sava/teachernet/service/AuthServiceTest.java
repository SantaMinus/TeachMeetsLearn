package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_PASS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
class AuthServiceTest {

  private final UserRepository repository = mock(UserRepository.class);
  private final AuthService authService = new AuthService(repository);

  @Test
  void loadUserByUsername() {
    when(repository.findByLogin(TEST_LOGIN))
        .thenReturn(Optional.of(
            User.builder()
                .id(1L).login(TEST_LOGIN).password(TEST_PASS).role(STUDENT.name()).build()));

    UserDetails result = authService.loadUserByUsername(TEST_LOGIN);

    assertNotNull(result);
    assertEquals(TEST_LOGIN, result.getUsername());
    assertEquals(TEST_PASS, result.getPassword());
    assertEquals(STUDENT.name(), result.getAuthorities().iterator().next().getAuthority());
  }

  @Test()
  void loadUserByUsernameThrowsUsernameNotFoundException() {
    assertThatThrownBy(() -> authService.loadUserByUsername("nonExistent"))
        .isInstanceOf(UsernameNotFoundException.class).hasMessage("User not found");
  }
}