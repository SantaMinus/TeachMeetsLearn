package com.sava.teachernet.service;

import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.mapper.StudentMapper;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.StudentRepository;
import com.sava.teachernet.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final TeacherRepository teacherRepository;

  public List<StudentDto> getAll() {
    return studentRepository.findAll()
        .stream()
        .map(studentMapper::toDto)
        .toList();
  }

  private Student getCurrentStudent() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    return studentRepository.findByUserLogin(userDetails.getUsername())
        .orElseThrow(() -> new EntityNotFoundException("Student not found"));
  }

  public StudentDto getCurrentStudentProfile() {
    return studentMapper.toDto(getCurrentStudent());
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

  public void assignTeacherToCurrentStudent(Long teacherId) {
    Student student = getCurrentStudent();
    Teacher teacher = teacherRepository.findById(teacherId)
        .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
    if (!student.getTeachers().contains(teacher)) {
      student.getTeachers().add(teacher);
      studentRepository.save(student);
    }
  }
}
