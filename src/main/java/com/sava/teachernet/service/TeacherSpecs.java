package com.sava.teachernet.service;

import com.sava.teachernet.dto.SearchDto;
import com.sava.teachernet.model.Teacher;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class TeacherSpecs {

  public static Specification<Teacher> bySearchDto(SearchDto dto) {
    return dto == null ? Specification.where(null)
        : Specification.where(nameContains(dto.getName()))
            .and(lastNameContains(dto.getLastName()))
            .and(subjectContains(dto.getSubject()))
            .and(locationContains(dto.getLocation()));
  }

  private static Specification<Teacher> nameContains(String name) {
    return isNullOrBlank(name) ? Specification.where(null)
        : (root, _, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
  }

  private static Specification<Teacher> lastNameContains(String lastName) {
    return isNullOrBlank(lastName) ? Specification.where(null)
        : (root, _, cb) -> cb.like(cb.lower(root.get("lastName")),
            "%" + lastName.toLowerCase() + "%");
  }

  private static Specification<Teacher> subjectContains(String subject) {
    return isNullOrBlank(subject) ? Specification.where(null)
        : (root, _, cb) -> cb.like(cb.lower(root.get("subject")),
            "%" + subject.toLowerCase() + "%");
  }

  private static Specification<Teacher> locationContains(String location) {
    return isNullOrBlank(location) ? Specification.where(null)
        : (root, _, cb) -> cb.like(cb.lower(root.get("location")),
            "%" + location.toLowerCase() + "%");
  }

  private static boolean isNullOrBlank(String value) {
    return value == null || value.isBlank();
  }
}