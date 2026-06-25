package com.sava.teachernet.service;

import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;
import static com.sava.teachernet.util.TestDataFactory.buildTestStudent;
import static com.sava.teachernet.util.TestDataFactory.createOauth2User;
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
import com.sava.teachernet.model.User;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class StudentServiceTest {

  @MockitoBean
  private StudentRepository studentRepository;
  @MockitoBean
  private TeacherRepository teacherRepository;
  @MockitoBean
  private ClientRegistrationRepository clientRegistrationRepository;
  @Autowired
  private StudentMapper mapper;
  @Autowired
  private StudentService studentService;

  @BeforeEach
  void setup() {
    setAuth();
  }

  @Test
  void testCreate() {
    User user = User.builder().login(TEST_LOGIN).build();
    Student student = buildTestStudent();
    when(studentRepository.save(any(Student.class))).thenReturn(student);

    StudentDto result = studentService.create(TEST_USER_NAME, TEST_USER_LAST_NAME, user);

    ArgumentCaptor<Student> studentArg = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository).save(studentArg.capture());
    assertEquals(TEST_USER_NAME, studentArg.getValue().getName());
    assertEquals(TEST_USER_LAST_NAME, studentArg.getValue().getLastName());
    assertEquals(TEST_LOGIN, studentArg.getValue().getUser().getLogin());
    assertEquals(TEST_LOGIN, result.getUserLogin());
  }

  @Test
  void testGetAll() {
    when(studentRepository.findAll()).thenReturn(List.of(buildTestStudent()));

    List<StudentDto> students = studentService.getAll();
    StudentDto student = students.getFirst();

    assertEquals(TEST_USER_NAME, student.getName());
    assertEquals(TEST_USER_LAST_NAME, student.getLastName());
    assertFalse(student.getTeachers().isEmpty());
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
  void testGetCurrentStudentProfileWithOauth2User() {
    setAuth(true);
    createOauth2User(TEST_USER_NAME, "ROLE_STUDENT");
    when(studentRepository.findByUserLogin(TEST_LOGIN))
        .thenReturn(Optional.of(buildTestStudent()));

    StudentDto student = studentService.getCurrentStudentProfile();

    assertEquals(TEST_LOGIN, student.getUserLogin());
  }

  @Test
  void testGetCurrentStudentProfileThrowsExceptionWhenNotFound() {
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> studentService.getCurrentStudentProfile())
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Student not found");
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
    List<Teacher> teachers = studentArg.getValue().getTeachers();
    assertFalse(teachers.isEmpty());
    assertEquals(1, teachers.size());
    assertEquals("new", teachers.getFirst().getUser().getLogin());
  }

  @Test
  void testAssignTeacherToCurrentStudentDoesNotAddExistingTeacher() {
    Student currentStudent = buildTestStudent();
    Teacher existingTeacher = currentStudent.getTeachers().getFirst();
    when(studentRepository.findByUserLogin(TEST_LOGIN))
        .thenReturn(Optional.of(currentStudent));
    when(teacherRepository.findById(existingTeacher.getId()))
        .thenReturn(Optional.of(existingTeacher));

    studentService.assignTeacherToCurrentStudent(existingTeacher.getId());

    verify(studentRepository, never()).save(any(Student.class));
  }

  @Test
  void testAssignTeacherToCurrentStudentThrowsEntityNotFoundException() {
    when(studentRepository.findByUserLogin(TEST_LOGIN))
        .thenReturn(Optional.of(buildTestStudent()));
    when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> studentService.assignTeacherToCurrentStudent(1L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Teacher not found");
    verify(studentRepository, never()).save(any(Student.class));
  }

  @Test
  void testUnassignTeacherFromCurrentStudent() {
    Student currentStudent = buildTestStudent();
    Teacher teacherToRemove = currentStudent.getTeachers().getFirst();
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(currentStudent));

    studentService.unassignTeacherFromCurrentStudent(teacherToRemove.getId());

    ArgumentCaptor<Student> studentArg = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository).save(studentArg.capture());
    assertTrue(studentArg.getValue().getTeachers().isEmpty());
  }

  @Test
  void testUnassignTeacherFromCurrentStudentWhenNotAssigned() {
    Student currentStudent = buildTestStudent();
    currentStudent.setTeachers(new ArrayList<>());
    when(studentRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(currentStudent));

    studentService.unassignTeacherFromCurrentStudent(99L);

    ArgumentCaptor<Student> studentArg = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository).save(studentArg.capture());
    assertTrue(studentArg.getValue().getTeachers().isEmpty());
  }
}
