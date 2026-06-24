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
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    Object principal = authentication.getPrincipal();

    String username = null;
    if (principal instanceof UserDetails userDetails) {
      username = userDetails.getUsername();
    } else if (principal instanceof OAuth2User oauth2User) {
      username = oauth2User.getAttribute("login");
    }

    return studentRepository.findByUserLogin(username)
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

  public void unassignTeacherFromCurrentStudent(Long teacherId) {
    Student student = getCurrentStudent();
    List<Teacher> found = student.getTeachers()
        .stream()
        .filter(t -> t.getId().equals(teacherId))
        .toList();
    student.getTeachers().removeAll(found);
    studentRepository.save(student);
  }
}
