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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private CustomOAuth2UserService customOAuth2UserService;

  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

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
    when(passwordEncoder.encode(any())).thenReturn("encoded-password");

    OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User);

    assertThat(result.getName()).isEqualTo(username);
    assertThat(result.getAuthorities()).extracting("authority")
        .contains(ROLE_PENDING_OAUTH2_REGISTRATION.name());
    verify(userRepository).save(userArgumentCaptor.capture());
    User savedUser = userArgumentCaptor.getValue();
    assertThat(savedUser.getLogin()).isEqualTo(username);
    assertThat(savedUser.getRole()).isEqualTo(ROLE_PENDING_OAUTH2_REGISTRATION.name());
    assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
    verify(passwordEncoder).encode(any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"TEACHER", "STUDENT"})
  void processOAuth2UserSetsAuthenticationInSecurityContext(String role) {
    String username = "ctxuser";
    User existingUser = new User();
    existingUser.setLogin(username);
    existingUser.setRole(role);
    OAuth2User oAuth2User = createOauth2User(username, role);
    when(userRepository.findByLogin(username)).thenReturn(Optional.of(existingUser));

    customOAuth2UserService.processOAuth2User(oAuth2User);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
        .extracting("authority")
        .contains(role);
  }

  @Test
  void processOAuth2UserForPendingUserSetsOnlyPendingAuthorityInContext() {
    String username = "newctxuser";
    OAuth2User oAuth2User = createOauth2User(username, "any_role");
    when(userRepository.findByLogin(username)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    when(passwordEncoder.encode(any())).thenReturn("enc");

    customOAuth2UserService.processOAuth2User(oAuth2User);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
        .extracting("authority")
        .containsExactly(ROLE_PENDING_OAUTH2_REGISTRATION.name());
  }

  @Test
  void processOAuth2UserWhenUserHasPendingRole_usesOnlyPendingAuthority() {
    String username = "pendinguser";
    User pendingUser = new User();
    pendingUser.setLogin(username);
    pendingUser.setRole(ROLE_PENDING_OAUTH2_REGISTRATION.name());
    OAuth2User oAuth2User = createOauth2User(username, ROLE_PENDING_OAUTH2_REGISTRATION.name());
    when(userRepository.findByLogin(username)).thenReturn(Optional.of(pendingUser));

    OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User);

    // Must use the PENDING authority, not the one embedded in the OAuth2User
    assertThat(result.getAuthorities()).extracting("authority")
        .containsExactly(ROLE_PENDING_OAUTH2_REGISTRATION.name());
    verify(userRepository, never()).save(any());
  }
}
