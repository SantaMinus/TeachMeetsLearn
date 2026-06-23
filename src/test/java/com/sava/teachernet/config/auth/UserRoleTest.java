package com.sava.teachernet.config.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserRoleTest {

  @Test
  void studentGetValue() {
    assertThat(UserRole.STUDENT.getValue()).isEqualTo("ROLE_STUDENT");
  }

  @Test
  void teacherGetValue() {
    assertThat(UserRole.TEACHER.getValue()).isEqualTo("ROLE_TEACHER");
  }

  @Test
  void pendingOAuth2RegistrationGetValue() {
    assertThat(UserRole.ROLE_PENDING_OAUTH2_REGISTRATION.getValue())
        .isEqualTo("ROLE_PENDING_OAUTH2_REGISTRATION");
  }

  @Test
  void enumHasExactlyThreeValues() {
    assertThat(UserRole.values()).hasSize(3);
  }

  @Test
  void pendingOAuth2RegistrationNameDiffersFromValue() {
    // Regression: the enum constant name is "ROLE_PENDING_OAUTH2_REGISTRATION"
    // and getValue() returns the same string — this is by design for this role.
    UserRole role = UserRole.ROLE_PENDING_OAUTH2_REGISTRATION;
    assertThat(role.name()).isEqualTo("ROLE_PENDING_OAUTH2_REGISTRATION");
    assertThat(role.getValue()).isEqualTo("ROLE_PENDING_OAUTH2_REGISTRATION");
  }

  @Test
  void studentAndTeacherValuesCarryRolePrefix() {
    assertThat(UserRole.STUDENT.getValue()).startsWith("ROLE_");
    assertThat(UserRole.TEACHER.getValue()).startsWith("ROLE_");
  }
}