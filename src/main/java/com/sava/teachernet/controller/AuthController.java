package com.sava.teachernet.controller;

import com.sava.teachernet.dto.SignInDto;
import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final AuthService service;

  @GetMapping("/signup")
  public String signUpForm() {
    return "signup";
  }

  @PostMapping("/signup")
  public String processRegistration(SignUpDto dto) {
    log.info("Got a signup request");
    service.signUp(dto);
    return "redirect:/auth/login";
  }

  @GetMapping("/login")
  public String signInForm() {
    return "login";
  }

  @PostMapping("/login")
  public ResponseEntity<Void> signIn(SignInDto data) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
    var authUser = authenticationManager.authenticate(usernamePassword);
    return ResponseEntity.ok().build();
  }
}
