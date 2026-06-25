package com.sava.teachernet.service;

import com.sava.teachernet.config.auth.UserRole;
import com.sava.teachernet.dto.RoleSelectionDto;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2RegistrationService {

  private final StudentService studentService;
  private final TeacherService teacherService;
  private final UserRepository userRepository;
  private final AuthService authService;

  public void processRoleSelection(RoleSelectionDto roleSelection) {
    getOauth2User().ifPresent(oauth2User -> {
      String login = oauth2User.getAttribute("login");
      userRepository.findByLogin(login).ifPresent(user -> {
        user.setRole(roleSelection.getRole());
        userRepository.save(user);
        createRoleEntity(roleSelection, oauth2User, user);
        authService.refreshAuthentication();
      });
    });
  }

  public boolean shouldShowRoleSelectionForm() {
    return getOauth2User().map(oauth2User -> {
      String login = oauth2User.getAttribute("login");
      User user = userRepository.findByLogin(login)
          .orElseThrow(
              () -> new IllegalStateException("User not found after OAuth2 authentication"));
      return Objects.equals(user.getRole(), UserRole.ROLE_PENDING_OAUTH2_REGISTRATION.getValue());
    }).orElse(false);
  }

  private void createRoleEntity(RoleSelectionDto roleSelection, OAuth2User oauth2User, User user) {
    String name = oauth2User.getAttribute("name");
    String login = oauth2User.getAttribute("login");
    String firstName = name != null ? name.split("\\s+")[0] : login;
    String lastName = name != null && name.split("\\s+").length > 1
        ? name.substring(firstName.length()).trim()
        : "";

    if (roleSelection.getRole().equals(UserRole.STUDENT.getValue())) {
      studentService.create(firstName, lastName, user);
    } else if (roleSelection.getRole().equals(UserRole.TEACHER.getValue())) {
      teacherService.create(firstName, lastName, user);
    }
  }

  private Optional<OAuth2User> getOauth2User() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
      return Optional.of((OAuth2User) authentication.getPrincipal());
    }
    return Optional.empty();
  }
}
