package com.sava.teachernet.mapper;

import com.sava.teachernet.dto.StudentShortDto;
import com.sava.teachernet.dto.TeacherDto;
import com.sava.teachernet.dto.TeacherShortDto;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

  @Mapping(target = "students", source = "students")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userLogin", source = "user.login")
  TeacherDto toDto(Teacher student);

  @Named("toTeacherShortDto")
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "lastName", source = "lastName")
  TeacherShortDto toShortDto(Teacher teacher);

  @Named("toStudentShortDto")
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "lastName", source = "lastName")
  StudentShortDto toStudentShortDto(Student student);
}
