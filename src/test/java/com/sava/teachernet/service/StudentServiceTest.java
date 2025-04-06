package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;
import static com.sava.teachernet.util.TestDataFactory.buildTestStudent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.mapper.StudentMapper;
import com.sava.teachernet.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@SpringBootTest
class StudentServiceTest {

  @MockBean
  private StudentRepository studentRepository;
  @Autowired
  private StudentMapper mapper;
  @Autowired
  private StudentService studentService;

  @Test
  void testGetAll() {
    when(studentRepository.findAll()).thenReturn(List.of(buildTestStudent()));

    assertEquals(TEST_USER_NAME, studentService.getAll().get(0).getName());
    assertEquals(TEST_USER_LAST_NAME, studentService.getAll().get(0).getLastName());
  }

  @Test
  void testGetCurrentStudentProfile() {
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(
            User.builder()
                .username(TEST_LOGIN)
                .authorities(STUDENT.getValue())
                .password("pass")
                .build(), null));
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(buildTestStudent()));

    StudentDto student = studentService.getCurrentStudentProfile();

    assertEquals(TEST_LOGIN, student.getUserLogin());
    assertEquals(TEST_USER_NAME, student.getName());
    assertEquals(TEST_USER_LAST_NAME, student.getLastName());
    assertFalse(student.getTeachers().isEmpty());
  }
}