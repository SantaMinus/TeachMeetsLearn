package com.sava.teachernet.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
}
