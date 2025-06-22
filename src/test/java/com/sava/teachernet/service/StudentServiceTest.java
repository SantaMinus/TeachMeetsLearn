package com.sava.teachernet.service;

import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;
import static com.sava.teachernet.util.TestDataFactory.buildTestStudent;
import static com.sava.teachernet.util.TestDataFactory.buildTestTeacher;
import static com.sava.teachernet.util.TestDataFactory.setAuth;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.mapper.StudentMapper;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.repository.StudentRepository;
import com.sava.teachernet.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class StudentServiceTest {

  @MockitoBean
  private StudentRepository studentRepository;
  @MockitoBean
  private TeacherRepository teacherRepository;
  @Autowired
  private StudentMapper mapper;
  @Autowired
  private StudentService studentService;

  @BeforeEach
  void setup() {
    setAuth();
  }

  @Test
  void testGetAll() {
    when(studentRepository.findAll()).thenReturn(List.of(buildTestStudent()));

    assertEquals(TEST_USER_NAME, studentService.getAll().getFirst().getName());
    assertEquals(TEST_USER_LAST_NAME, studentService.getAll().getFirst().getLastName());
  }

  @Test
  void testGetCurrentStudentProfile() {
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(buildTestStudent()));

    StudentDto student = studentService.getCurrentStudentProfile();

    assertEquals(TEST_LOGIN, student.getUserLogin());
    assertEquals(TEST_USER_NAME, student.getName());
    assertEquals(TEST_USER_LAST_NAME, student.getLastName());
    assertFalse(student.getTeachers().isEmpty());
  }

  @Test
  void testAssignTeacherToCurrentStudentAddsNewTeacher() {
    Student currentStudent = buildTestStudent();
    currentStudent.setTeachers(new ArrayList<>());
    Teacher newTeacher = Teacher.builder()
        .name("New")
        .lastName("Teacher")
        .user(com.sava.teachernet.model.User.builder()
            .id(1L)
            .login("new")
            .build())
        .build();
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(currentStudent));
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(newTeacher));

    studentService.assignTeacherToCurrentStudent(1L);

    ArgumentCaptor<Student> studentArg = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository).save(studentArg.capture());
    assertFalse(studentArg.getValue().getTeachers().isEmpty());
    assertEquals(1, studentArg.getValue().getTeachers().size());
    assertEquals("new", studentArg.getValue().getTeachers().getFirst().getUser().getLogin());
  }

  @Test
  void testAssignTeacherToCurrentStudentThrowsEntityNotFoundException() {
    Student currentStudent = buildTestStudent();
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(currentStudent));
    when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() ->
        studentService.assignTeacherToCurrentStudent(1L))
        .isInstanceOf(EntityNotFoundException.class).hasMessage("Teacher not found");
    verify(studentRepository, never()).save(any(Student.class));
  }

  @Test
  void testAssignTeacherToCurrentStudentDoesntAddExistingTeacher() {
    Student currentStudent = buildTestStudent();
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(currentStudent));
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(buildTestTeacher()));

    studentService.assignTeacherToCurrentStudent(1L);

    verify(studentRepository, never()).save(any(Student.class));
  }

  @Test
  void testUnassignTeacherFromCurrentStudentRemovesTeacher() {
    Student currentStudent = buildTestStudent();
    currentStudent.setTeachers(new ArrayList<>(currentStudent.getTeachers()));
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(currentStudent));

    studentService.unassignTeacherFromCurrentStudent(1L);

    ArgumentCaptor<Student> studentArg = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository).save(studentArg.capture());
    assertTrue(studentArg.getValue().getTeachers().isEmpty());
  }
}
