package com.sava.teachernet.util;

import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;

import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.model.User;
import java.util.List;

public class TestDataFactory {

  public static Student buildTestStudent() {
    return Student.builder()
        .name(TEST_USER_NAME)
        .lastName(TEST_USER_LAST_NAME)
        .user(User.builder()
            .login(TEST_LOGIN)
            .build())
        .teachers(List.of(buildTestTeacher()))
        .build();
  }

  public static Teacher buildTestTeacher() {
    return Teacher.builder()
        .name(TEST_USER_NAME)
        .lastName(TEST_USER_LAST_NAME)
        .user(User.builder()
            .login(TEST_LOGIN)
            .build())
        .build();
  }
}
