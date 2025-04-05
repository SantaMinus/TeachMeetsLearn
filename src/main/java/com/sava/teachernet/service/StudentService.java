package com.sava.teachernet.service;

import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.mapper.StudentMapper;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.StudentRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;

  public List<StudentDto> getAll() {
    return studentRepository.findAll()
        .stream()
        .map(studentMapper::toDto)
        .toList();
  }

  public StudentDto getProfile(String username) {
    return studentMapper.toDto(studentRepository.findByUserLogin(username).orElse(null));
  }

  public StudentDto create(String name, String lastName, User user) {
    Student student = Student.builder()
        .name(name)
        .lastName(lastName)
        .user(user)
        .dateJoined(LocalDate.now())
        .build();
    return studentMapper.toDto(studentRepository.save(student));
  }

  public Student update(Student student) {
    return null;
  }

  public void delete(Student student) {

  }
}
