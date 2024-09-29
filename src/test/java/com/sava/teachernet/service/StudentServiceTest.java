package com.sava.teachernet.service;

import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;
import static com.sava.teachernet.util.TestDataFactory.buildTestStudent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sava.teachernet.model.Student;
import com.sava.teachernet.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StudentServiceTest {

  private final StudentRepository studentRepository = mock(StudentRepository.class);
  private final StudentService studentService = new StudentService(studentRepository);

  @Test
  void testGetAll() {
    when(studentRepository.findAll()).thenReturn(List.of(buildTestStudent()));

    assertEquals(TEST_USER_NAME, studentService.getAll().get(0).getName());
    assertEquals(TEST_USER_LAST_NAME, studentService.getAll().get(0).getLastName());
  }

  @Test
  void testGetProfile() {
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(buildTestStudent()));

    Student student = studentService.getProfile(TEST_LOGIN);
    assertEquals(TEST_LOGIN, student.getUser().getLogin());
    assertEquals(TEST_USER_NAME, student.getName());
    assertEquals(TEST_USER_LAST_NAME, student.getLastName());
  }
}