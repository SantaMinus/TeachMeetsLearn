package com.sava.teachernet.mapper;

import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;
import static com.sava.teachernet.util.TestDataFactory.buildTestStudent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.model.Student;
import java.util.List;
import org.junit.jupiter.api.Test;

class StudentMapperTest {

  private final StudentMapperImpl studentMapper = new StudentMapperImpl();

  @Test
  void testToDtoMapsStudentDtoWithTeachers() {
    Student student = buildTestStudent();

    StudentDto dto = studentMapper.toDto(student);

    assertEquals(TEST_USER_NAME, dto.getName());
    assertEquals(TEST_USER_LAST_NAME, dto.getLastName());
    assertEquals(TEST_LOGIN, dto.getUserLogin());
    assertEquals(1L, dto.getUserId());
    assertEquals(1, dto.getTeachers().size());
  }

  @Test
  void testToDtoMapsStudentDtoWithNoTeachers() {
    Student student = buildTestStudent();
    student.setTeachers(null);

    StudentDto dto = studentMapper.toDto(student);

    assertEquals(TEST_USER_NAME, dto.getName());
    assertEquals(TEST_USER_LAST_NAME, dto.getLastName());
    assertEquals(TEST_LOGIN, dto.getUserLogin());
    assertEquals(1L, dto.getUserId());
    assertNull(dto.getTeachers());
  }

  @Test
  void testToDtoMapsStudentDtoWithNoUserNoTeachers() {
    Student student = Student.builder()
        .name(TEST_USER_NAME)
        .lastName(TEST_USER_LAST_NAME)
        .teachers(List.of())
        .build();

    StudentDto dto = studentMapper.toDto(student);

    assertEquals(TEST_USER_NAME, dto.getName());
    assertEquals(TEST_USER_LAST_NAME, dto.getLastName());
    assertNull(dto.getUserLogin());
    assertNull(dto.getUserId());
    assertTrue(dto.getTeachers().isEmpty());
  }

  @Test
  void testToDtoDoesntMapNull() {
    assertNull(studentMapper.toDto(null));
  }
}