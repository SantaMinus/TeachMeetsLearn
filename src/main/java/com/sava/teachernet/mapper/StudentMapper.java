package com.sava.teachernet.mapper;

import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.dto.TeacherShortDto;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
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
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "lastName", source = "lastName")
  TeacherShortDto toTeacherShortDto(Teacher teacher);
}
