package com.sava.teachernet.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class User {

  String name;
  String lastName;
  LocalDate birthDate;
  LocalDate dateJoined;
  List<String> courses;
}
