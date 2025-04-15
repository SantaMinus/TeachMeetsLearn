package com.sava.teachernet.dto;

import com.sava.teachernet.model.Teacher;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeacherDto {

  private Long id;
  private String name;
  private String lastName;
  private LocalDate dateJoined;
  private Set<StudentDto> students;

  public TeacherDto(Teacher teacher) {
    this.id = teacher.getId();
    this.name = teacher.getName();
    this.lastName = teacher.getLastName();
    this.students = new HashSet<>();
  }
}
