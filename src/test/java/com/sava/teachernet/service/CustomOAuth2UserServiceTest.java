package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.ROLE_PENDING_OAUTH2_REGISTRATION;
import static com.sava.teachernet.util.TestDataFactory.createOauth2User;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomOAuth2UserService customOAuth2UserService;

  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;

  @ParameterizedTest
  @ValueSource(strings = {"TEACHER", "STUDENT"})
  void processOAuth2UserReturnsExistingUser(String role) {
    String username = "testuser";
    User existingUser = new User();
    existingUser.setLogin(username);
    existingUser.setRole(role);

    OAuth2User oAuth2User = createOauth2User(username, role);

    when(userRepository.findByLogin(username)).thenReturn(Optional.of(existingUser));

    OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User);

    assertThat(result.getName()).isEqualTo(username);
    assertThat(result.getAuthorities()).extracting("authority")
        .contains(role);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void processOAuth2UserCreatesNewUser() {
    String username = "newuser";
    OAuth2User oAuth2User = createOauth2User(username, "some_oauth_role");
    when(userRepository.findByLogin(username)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User);

    assertThat(result.getName()).isEqualTo(username);
    assertThat(result.getAuthorities()).extracting("authority")
        .contains(ROLE_PENDING_OAUTH2_REGISTRATION.name());
    verify(userRepository).save(userArgumentCaptor.capture());
    User savedUser = userArgumentCaptor.getValue();
    assertThat(savedUser.getLogin()).isEqualTo(username);
    assertThat(savedUser.getRole()).isEqualTo(ROLE_PENDING_OAUTH2_REGISTRATION.name());
  }
}
