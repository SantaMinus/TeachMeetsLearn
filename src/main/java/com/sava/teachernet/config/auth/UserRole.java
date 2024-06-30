package com.sava.teachernet.config.auth;

public enum UserRole {
  STUDENT("student"),
  TEACHER("teacher");

  private final String role;

  UserRole(String role) {
    this.role = role;
  }

  public String getValue() {
    return role;
  }
}
