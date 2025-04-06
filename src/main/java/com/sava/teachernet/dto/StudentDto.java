package com.sava.teachernet.dto;

import java.time.LocalDate;
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
  private Set<TeacherShortDto> teachers;
}
