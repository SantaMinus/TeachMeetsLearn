package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.ROLE_PENDING_OAUTH2_REGISTRATION;
import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static com.sava.teachernet.config.auth.UserRole.TEACHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sava.teachernet.dto.RoleSelectionDto;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class OAuth2RegistrationServiceTest {

  @Mock
  private StudentService studentService;
  @Mock
  private TeacherService teacherService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private AuthService authService;

  @InjectMocks
  private OAuth2RegistrationService oAuth2RegistrationService;

  @Captor
  private ArgumentCaptor<User> userCaptor;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldShowRoleSelectionForm_returnsTrueWhenPendingRegistration() {
    String login = "ghuser";
    setOAuth2Principal(login);
    User user = new User();
    user.setLogin(login);
    user.setRole(ROLE_PENDING_OAUTH2_REGISTRATION.getValue());
    when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

    boolean result = oAuth2RegistrationService.shouldShowRoleSelectionForm();

    assertThat(result).isTrue();
  }

  @Test
  void shouldShowRoleSelectionForm_returnsFalseWhenRoleAlreadyAssigned() {
    String login = "ghuser";
    setOAuth2Principal(login);
    User user = new User();
    user.setLogin(login);
    user.setRole(STUDENT.getValue());
    when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

    boolean result = oAuth2RegistrationService.shouldShowRoleSelectionForm();

    assertThat(result).isFalse();
  }

  @Test
  void shouldShowRoleSelectionForm_returnsFalseWhenNoPrincipal() {
    SecurityContextHolder.clearContext();

    boolean result = oAuth2RegistrationService.shouldShowRoleSelectionForm();

    assertThat(result).isFalse();
  }

  @Test
  void shouldShowRoleSelectionForm_throwsWhenUserNotFoundInDb() {
    String login = "ghost";
    setOAuth2Principal(login);
    when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> oAuth2RegistrationService.shouldShowRoleSelectionForm())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("User not found after OAuth2 authentication");
  }

  @Test
  void processRoleSelection_assignsStudentRoleAndCreatesStudentEntity() {
    String login = "ghuser";
    setOAuth2PrincipalWithName(login, "Jane Doe");
    User user = pendingUser(login);
    when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    RoleSelectionDto dto = new RoleSelectionDto();
    dto.setRole(STUDENT.getValue());

    oAuth2RegistrationService.processRoleSelection(dto);

    verify(userRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getRole()).isEqualTo(STUDENT.getValue());
    verify(studentService).create(eq("Jane"), eq("Doe"), any(User.class));
    verify(teacherService, never()).create(any(), any(), any());
    verify(authService).refreshAuthentication();
  }

  @Test
  void processRoleSelection_assignsTeacherRoleAndCreatesTeacherEntity() {
    String login = "ghuser";
    setOAuth2PrincipalWithName(login, "John Smith");
    User user = pendingUser(login);
    when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    RoleSelectionDto dto = new RoleSelectionDto();
    dto.setRole(TEACHER.getValue());

    oAuth2RegistrationService.processRoleSelection(dto);

    verify(userRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getRole()).isEqualTo(TEACHER.getValue());
    verify(teacherService).create(eq("John"), eq("Smith"), any(User.class));
    verify(studentService, never()).create(any(), any(), any());
    verify(authService).refreshAuthentication();
  }

  @Test
  void processRoleSelection_doesNothingWhenNoPrincipal() {
    SecurityContextHolder.clearContext();
    RoleSelectionDto dto = new RoleSelectionDto();
    dto.setRole(STUDENT.getValue());

    oAuth2RegistrationService.processRoleSelection(dto);

    verify(userRepository, never()).save(any());
    verify(studentService, never()).create(any(), any(), any());
    verify(authService, never()).refreshAuthentication();
  }

  @Test
  void processRoleSelection_doesNothingWhenUserNotFoundInDb() {
    String login = "ghost";
    setOAuth2Principal(login);
    when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

    RoleSelectionDto dto = new RoleSelectionDto();
    dto.setRole(STUDENT.getValue());

    oAuth2RegistrationService.processRoleSelection(dto);

    verify(userRepository, never()).save(any());
    verify(studentService, never()).create(any(), any(), any());
    verify(authService, never()).refreshAuthentication();
  }

  @Test
  void processRoleSelection_usesLoginAsFirstNameWhenNameAttributeIsNull() {
    String login = "namenull";
    setOAuth2PrincipalNoName(login);
    User user = pendingUser(login);
    when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    RoleSelectionDto dto = new RoleSelectionDto();
    dto.setRole(STUDENT.getValue());

    oAuth2RegistrationService.processRoleSelection(dto);

    verify(studentService).create(eq(login), eq(""), any(User.class));
  }

  @Test
  void processRoleSelection_handlesSingleWordName() {
    String login = "mononym";
    setOAuth2PrincipalWithName(login, "Madonna");
    User user = pendingUser(login);
    when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    RoleSelectionDto dto = new RoleSelectionDto();
    dto.setRole(STUDENT.getValue());

    oAuth2RegistrationService.processRoleSelection(dto);

    verify(studentService).create(eq("Madonna"), eq(""), any(User.class));
  }

  private void setOAuth2Principal(String login) {
    OAuth2User oAuth2User = buildOAuth2User(Map.of("login", login));
    setOAuth2AuthToken(oAuth2User);
  }

  private void setOAuth2PrincipalWithName(String login, String name) {
    OAuth2User oAuth2User = buildOAuth2User(Map.of("login", login, "name", name));
    setOAuth2AuthToken(oAuth2User);
  }

  private void setOAuth2PrincipalNoName(String login) {
    OAuth2User oAuth2User = buildOAuth2User(Map.of("login", login));
    setOAuth2AuthToken(oAuth2User);
  }

  private OAuth2User buildOAuth2User(Map<String, Object> attributes) {
    return new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority(ROLE_PENDING_OAUTH2_REGISTRATION.getValue())),
        attributes,
        "login");
  }

  private void setOAuth2AuthToken(OAuth2User oAuth2User) {
    SecurityContextHolder.getContext().setAuthentication(
        new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), "test-client"));
  }

  private User pendingUser(String login) {
    User u = new User();
    u.setLogin(login);
    u.setRole(ROLE_PENDING_OAUTH2_REGISTRATION.getValue());
    return u;
  }
}