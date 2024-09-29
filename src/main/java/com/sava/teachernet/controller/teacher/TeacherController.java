package com.sava.teachernet.controller.teacher;

import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {
  private final TeacherService teacherService;

  @GetMapping("/dashboard")
  public String showTeacherDashboard() {
    return "teacher/dashboard";
  }

  @GetMapping("/profile")
  public String showStudentProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Teacher teacher = teacherService.getProfile(userDetails.getUsername());

    model.addAttribute("teacher", teacher);
    return "teacher/profile";
  }
}
