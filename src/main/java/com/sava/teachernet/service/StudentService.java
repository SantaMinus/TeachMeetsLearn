package com.sava.teachernet.service;

import com.sava.teachernet.repository.StudentRepository;
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
  public Student getProfile(String username) {
    return studentRepository.findByUserLogin(username).orElse(null);
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
