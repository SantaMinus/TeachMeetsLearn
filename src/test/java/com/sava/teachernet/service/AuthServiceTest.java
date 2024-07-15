package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_PASS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sava.teachernet.config.auth.UserRole;
import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.exception.InvalidAuthException;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
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

  @Test
  void testSignUpThrowsInvalidAuthException() {
    SignUpDto signUpDto = new SignUpDto(TEST_LOGIN, TEST_PASS, STUDENT);
    when(repository.findByLogin(TEST_LOGIN))
        .thenReturn(Optional.of(
            User.builder()
                .id(1L).login(TEST_LOGIN).password(TEST_PASS).role(STUDENT.name()).build()));

    assertThatThrownBy(() -> authService.signUp(signUpDto))
        .isInstanceOf(InvalidAuthException.class).hasMessage("Username already exists");
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testSignUp(UserRole role) {
    SignUpDto signUpDto = new SignUpDto(TEST_LOGIN, TEST_PASS, role);

    authService.signUp(signUpDto);

    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
    verify(repository).save(argument.capture());
    assertEquals(TEST_LOGIN, argument.getValue().getLogin());
    assertEquals(role.getValue(), argument.getValue().getRole());
  }
}
