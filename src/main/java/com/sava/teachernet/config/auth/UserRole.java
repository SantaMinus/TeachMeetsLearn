package com.sava.teachernet.config.auth;

public enum UserRole {
  STUDENT("ROLE_STUDENT"),
  TEACHER("ROLE_TEACHER"),
  ROLE_PENDING_OAUTH2_REGISTRATION("ROLE_PENDING_OAUTH2_REGISTRATION");

  private final String role;

  UserRole(String role) {
    this.role = role;
  }

  public String getValue() {
    return role;
  }
}
