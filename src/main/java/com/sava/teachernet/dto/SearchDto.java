package com.sava.teachernet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
  private String name;
  private String lastName;
  private String subject;
  private String location;
}
