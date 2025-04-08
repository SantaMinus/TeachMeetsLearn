package com.sava.teachernet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TeacherShortDto {

  private Long id;
  private String name;
  private String lastName;
}
