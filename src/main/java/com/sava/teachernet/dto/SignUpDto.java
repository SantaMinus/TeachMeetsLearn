package com.sava.teachernet.dto;

import com.sava.teachernet.config.auth.UserRole;

public record SignUpDto(
    String login,
    String password,
    UserRole role,
    String name,
    String lastName) {
}