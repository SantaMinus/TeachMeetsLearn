package com.sava.teachernet.controller.teacher;

import com.sava.teachernet.dto.SearchDto;
import com.sava.teachernet.dto.TeacherDto;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.service.TeacherService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

  private final TeacherService teacherService;

  @GetMapping
  public String getTeachers(Model model) {
    List<Teacher> teacherList = teacherService.getAll();
    model.addAttribute("teacherList", teacherList);

    return "teachers";
  }

  @GetMapping("/search")
  public String getTeachersByQuery(@ModelAttribute("searchDto") SearchDto searchDto, Model model) {
    List<TeacherDto> teacherList = teacherService.getByQuery(searchDto);
    model.addAttribute("teacherList", teacherList);

    return "students/teachers";
  }

  @GetMapping("/me/dashboard")
  public String showCurrentTeacherDashboard() {
    return "teacher/dashboard";
  }

  @GetMapping("/me/profile")
  public String showCurrentTeacherProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Teacher teacher = teacherService.getProfile(userDetails.getUsername());

    model.addAttribute("teacher", teacher);
    return "teacher/profile";
  }

  @GetMapping("/me/students")
  public String getCurrentTeacherStudents(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Teacher teacher = teacherService.getProfile(userDetails.getUsername());
    List<Student> teacherStudents = teacher.getStudents();

    model.addAttribute("studentList", teacherStudents);
    return "teacher/students";
  }
}
