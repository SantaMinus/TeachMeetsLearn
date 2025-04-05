package com.sava.teachernet.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class StudentShortDto {

  private Long id;
  private String name;
  private String lastName;
  private LocalDate dateJoined;
}
