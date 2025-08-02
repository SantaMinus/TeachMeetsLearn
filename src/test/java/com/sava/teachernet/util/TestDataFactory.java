package com.sava.teachernet.util;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;

import com.sava.teachernet.config.auth.UserRole;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.model.User;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class TestDataFactory {

  public static Student buildTestStudent() {
    return Student.builder()
        .name(TEST_USER_NAME)
        .lastName(TEST_USER_LAST_NAME)
        .user(User.builder()
            .id(1L)
            .login(TEST_LOGIN)
            .build())
        .teachers(List.of(buildTestTeacher()))
        .build();
  }

  public static Teacher buildTestTeacher() {
    return Teacher.builder()
        .id(1L)
        .name(TEST_USER_NAME)
        .lastName(TEST_USER_LAST_NAME)
        .user(User.builder()
            .login(TEST_LOGIN)
            .build())
        .build();
  }

  public static void setAuth() {
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(
            org.springframework.security.core.userdetails.User.builder()
                .username(TEST_LOGIN)
                .authorities(STUDENT.getValue())
                .password("pass")
                .build(), null));
  }

  public static User buildTestUser() {
    return User.builder()
        .login("testUser")
        .password("password")
        .role(UserRole.STUDENT.getValue())
        .build();
  }

  public static OAuth2User createOauth2User(String username, String role) {
    Map<String, Object> attributes = Map.of("login", username);
    var authorities = List.of(new SimpleGrantedAuthority(role));
    return new DefaultOAuth2User(authorities, attributes, "login");
  }
}
