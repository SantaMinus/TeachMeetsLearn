package com.sava.teachernet.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@MappedSuperclass
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String lastName;
  private LocalDate dateJoined;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;
}
