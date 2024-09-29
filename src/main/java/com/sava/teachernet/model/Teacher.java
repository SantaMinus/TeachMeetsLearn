package com.sava.teachernet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Teacher extends AbstractUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

//  @OneToOne
//  @JoinColumn(name = "user_id", referencedColumnName = "id")
//  private User user;

  @ManyToMany(mappedBy = "teachers")
  List<Student> students;
}
