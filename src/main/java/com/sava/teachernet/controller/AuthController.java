package com.sava.teachernet.controller;

import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;

  @GetMapping("/signup")
  public String signUpForm() {
    return "signup";
  }

  @PostMapping("/signup")
  public String processRegistration(SignUpDto dto) {
    service.signUp(dto);
    return "redirect:/auth/login";
  }

  @GetMapping("/login")
  public String signInForm() {
    return "login";
  }
}
