package com.sava.teachernet.config.auth;

public enum UserRole {
  STUDENT("ROLE_STUDENT"),
  TEACHER("ROLE_TEACHER");

  private final String role;

  UserRole(String role) {
    this.role = role;
  }

  public String getValue() {
    return role;
  }
}
