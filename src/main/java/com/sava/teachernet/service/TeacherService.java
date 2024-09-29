package com.sava.teachernet.service;

import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.TeacherRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService implements UserService<Teacher> {

  private final TeacherRepository teacherRepository;

  @Override
  public List<Teacher> getAll() {
    return teacherRepository.findAll();
  }

  @Override
  public Teacher getProfile(String username) {
    return teacherRepository.findByUserLogin(username).orElse(null);
  }

  @Override
  public Teacher create(String name, String lastName, User user) {
    Teacher teacher = Teacher.builder()
        .name(name)
        .lastName(lastName)
        .user(user)
        .dateJoined(LocalDate.now())
        .build();
    return teacherRepository.save(teacher);
  }

  @Override
  public Teacher update(Teacher student) {
    return null;
  }

  @Override
  public void delete(Teacher student) {

  }
}
