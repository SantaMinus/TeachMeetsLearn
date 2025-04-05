package com.sava.teachernet.dto;

import com.sava.teachernet.model.Student;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentDto {

  private Long id;
  private String name;
  private Long userId;
  private String userLogin;
  private String lastName;
  private LocalDate dateJoined;
  private Set<TeacherDto> teachers;

  public StudentDto(Student student) {
    this.id = student.getId();
    this.name = student.getName();
    this.lastName = student.getLastName();
    this.teachers = new HashSet<>();
  }
}
