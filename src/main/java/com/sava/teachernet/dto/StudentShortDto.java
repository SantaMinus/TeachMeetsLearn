package com.sava.teachernet.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class StudentShortDto {

  private Long id;
  private String name;
  private String lastName;
  private LocalDate dateJoined;
}
