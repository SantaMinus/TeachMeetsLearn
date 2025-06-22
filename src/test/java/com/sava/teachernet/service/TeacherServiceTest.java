package com.sava.teachernet.service;

import static com.sava.teachernet.util.Constants.TEST_LOGIN;
import static com.sava.teachernet.util.Constants.TEST_USER_LAST_NAME;
import static com.sava.teachernet.util.Constants.TEST_USER_NAME;
import static com.sava.teachernet.util.TestDataFactory.buildTestTeacher;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.sava.teachernet.mapper.TeacherMapper;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.repository.TeacherRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class TeacherServiceTest {

  @MockitoBean
  private TeacherRepository teacherRepository;
  @MockitoBean
  private TeacherMapper teacherMapper;
  @Autowired
  private TeacherService teacherService;

  @Test
  void testGetAll() {
    when(teacherRepository.findAll()).thenReturn(List.of(buildTestTeacher()));

    assertEquals(TEST_USER_NAME, teacherService.getAll().getFirst().getName());
    assertEquals(TEST_USER_LAST_NAME, teacherService.getAll().getFirst().getLastName());
  }

  @Test
  void testGetProfile() {
    when(teacherRepository.findByUserLogin(TEST_LOGIN)).thenReturn(Optional.of(buildTestTeacher()));

    Teacher teacher = teacherService.getProfile(TEST_LOGIN);
    assertEquals(TEST_LOGIN, teacher.getUser().getLogin());
    assertEquals(TEST_USER_NAME, teacher.getName());
    assertEquals(TEST_USER_LAST_NAME, teacher.getLastName());
  }
}
