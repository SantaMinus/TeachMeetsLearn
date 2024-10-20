package com.sava.teachernet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TeacherNetController {

  @GetMapping("/")
  public String showWelcomePage() {
    return "teacher-portal";
  }

  @PostMapping("/teacher-portal")
  public ModelAndView selectRole(@RequestParam("role") String role) {
    ModelAndView modelAndView = new ModelAndView();

    if ("student".equals(role)) {
      modelAndView.setViewName("redirect:/students/me/dashboard");
    } else if ("teacher".equals(role)) {
      modelAndView.setViewName("redirect:/teachers/me/dashboard");
    } else {
      modelAndView.setViewName("redirect:/error");
    }

    return modelAndView;
  }
}
