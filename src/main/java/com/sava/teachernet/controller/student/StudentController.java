package com.sava.teachernet.controller.student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

  @GetMapping("/student/dashboard")
  public String showStudentDashboard() {
    return "student/dashboard";
  }
}

