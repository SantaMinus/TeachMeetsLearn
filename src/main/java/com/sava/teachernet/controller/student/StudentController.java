package com.sava.teachernet.controller.student;

import com.sava.teachernet.model.Student;
import com.sava.teachernet.service.StudentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;

  @GetMapping("/dashboard")
  public String showStudentDashboard() {
    return "student/dashboard";
  }

  @GetMapping("/profile")
  public String showStudentProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Student student = studentService.getProfile(userDetails.getUsername());

    model.addAttribute("student", student);
    return "student/profile";
  }

  @GetMapping
  public String getStudents(Model model) {
    List<Student> studentList = studentService.getAll();
    model.addAttribute("studentList", studentList);

    return "/students";
  }
}

