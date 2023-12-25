package com.sava.teachernet.service;

import com.sava.teachernet.controller.repository.StudentRepository;
import com.sava.teachernet.model.Student;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService implements UserService<Student> {
  private final StudentRepository studentRepository;

  @Override
  public List<Student> getAll() {
    return studentRepository.findAll();
  }

  @Override
  public Student getProfile(int id) {
    return null;
  }

  @Override
  public Student create(Student student) {
    return null;
  }

  @Override
  public Student update(Student student) {
    return null;
  }

  @Override
  public void delete(Student student) {

  }
}
