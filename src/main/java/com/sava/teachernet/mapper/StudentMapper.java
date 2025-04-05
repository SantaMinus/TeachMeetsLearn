package com.sava.teachernet.mapper;

import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.dto.TeacherShortDto;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import java.util.Set;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudentMapper {

  @Mapping(target = "teachers", source = "teachers", qualifiedByName = "toTeacherShortDto")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userLogin", source = "user.login")
  StudentDto toDto(Student student);

  @Named("toTeacherShortDto")
  default TeacherShortDto toTeacherShortDto(Teacher teacher) {
    return new TeacherShortDto(
        teacher.getId(),
        teacher.getName(),
        teacher.getLastName(),
        teacher.getDateJoined());
  }

  @IterableMapping(qualifiedByName = "toTeacherShortDto")
  Set<TeacherShortDto> mapTeachers(Set<Teacher> teachers);
}
