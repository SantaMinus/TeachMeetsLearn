package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static com.sava.teachernet.config.auth.UserRole.TEACHER;
import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_PASS;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.exception.InvalidAuthException;
import com.sava.teachernet.mapper.StudentMapper;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.StudentRepository;
import com.sava.teachernet.repository.TeacherRepository;
import com.sava.teachernet.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
class AuthServiceTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final StudentRepository studentRepository = mock(StudentRepository.class);
  private final TeacherRepository teacherRepository = mock(TeacherRepository.class);
  private final StudentMapper mapper = mock(StudentMapper.class);
  private final StudentService studentService = new StudentService(studentRepository, mapper);
  private final TeacherService teacherService = new TeacherService(teacherRepository);
  private final AuthService authService = new AuthService(userRepository, studentService,
      teacherService);

  @Test
  void loadUserByUsername() {
    when(userRepository.findByLogin(TEST_LOGIN))
        .thenReturn(Optional.of(
            User.builder()
                .id(1).login(TEST_LOGIN).password(TEST_PASS).role(STUDENT.name()).build()));

    UserDetails result = authService.loadUserByUsername(TEST_LOGIN);

    assertNotNull(result);
    assertEquals(TEST_LOGIN, result.getUsername());
    assertEquals(TEST_PASS, result.getPassword());
    assertEquals(STUDENT.name(), result.getAuthorities().iterator().next().getAuthority());
  }

  @Test()
  void loadUserByUsernameThrowsUsernameNotFoundException() {
    assertThatThrownBy(() -> authService.loadUserByUsername("nonExistent"))
        .isInstanceOf(UsernameNotFoundException.class).hasMessage("User not found");
  }

  @Test
  void testSignUpExistingUsernameThrowsInvalidAuthException() {
    SignUpDto signUpDto = new SignUpDto(TEST_LOGIN, TEST_PASS, STUDENT, TEST_USER_NAME,
        TEST_USER_LAST_NAME);
    when(userRepository.findByLogin(TEST_LOGIN))
        .thenReturn(Optional.of(
            User.builder()
                .id(1).login(TEST_LOGIN).password(TEST_PASS).role(STUDENT.name()).build()));

    assertThatThrownBy(() -> authService.signUp(signUpDto))
        .isInstanceOf(InvalidAuthException.class).hasMessage("Username already exists");
  }

  @Test
  void testSignUpEmptyRoleThrowsInvalidAuthException() {
    SignUpDto signUpDto = new SignUpDto(TEST_LOGIN, TEST_PASS, null, TEST_USER_NAME,
        TEST_USER_LAST_NAME);

    assertThatThrownBy(() -> authService.signUp(signUpDto))
        .isInstanceOf(InvalidAuthException.class).hasMessage("Role is not specified");
  }

  @Test
  void testSignUpStudent() {
    SignUpDto signUpDto = new SignUpDto(TEST_LOGIN, TEST_PASS, STUDENT, TEST_USER_NAME,
        TEST_USER_LAST_NAME);

    authService.signUp(signUpDto);

    ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userArg.capture());
    assertEquals(TEST_LOGIN, userArg.getValue().getLogin());
    assertEquals(STUDENT.getValue(), userArg.getValue().getRole());

    ArgumentCaptor<Student> studentArg = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository).save(studentArg.capture());
    assertEquals(TEST_USER_NAME, studentArg.getValue().getName());
    assertEquals(TEST_USER_LAST_NAME, studentArg.getValue().getLastName());
  }

  @Test
  void testSignUpTeacher() {
    SignUpDto signUpDto = new SignUpDto(TEST_LOGIN, TEST_PASS, TEACHER, TEST_USER_NAME,
        TEST_USER_LAST_NAME);

    authService.signUp(signUpDto);

    ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userArg.capture());
    assertEquals(TEST_LOGIN, userArg.getValue().getLogin());
    assertEquals(TEACHER.getValue(), userArg.getValue().getRole());

    ArgumentCaptor<Teacher> teacherArg = ArgumentCaptor.forClass(Teacher.class);
    verify(teacherRepository).save(teacherArg.capture());
    assertEquals(TEST_USER_NAME, teacherArg.getValue().getName());
    assertEquals(TEST_USER_LAST_NAME, teacherArg.getValue().getLastName());
  }
}
