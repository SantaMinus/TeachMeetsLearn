package com.sava.teachernet.controller.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherController {

  @GetMapping("/teacher/dashboard")
  public String showTeacherDashboard() {
    return "teacher/dashboard";
  }

}
