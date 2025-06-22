package com.sava.teachernet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {

  private String name;
  private String lastName;
  private String subject;
  private String location;
}
