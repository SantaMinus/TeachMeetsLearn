package com.sava.teachernet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class TeacherShortDto {

  private Long id;
  private String name;
  private String lastName;
}
