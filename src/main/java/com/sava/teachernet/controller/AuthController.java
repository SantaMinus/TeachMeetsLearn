package com.sava.teachernet.controller;

import com.sava.teachernet.config.auth.TokenProvider;
import com.sava.teachernet.dto.JwtDto;
import com.sava.teachernet.dto.SignInDto;
import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.model.User;
import com.sava.teachernet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final AuthService service;
  private final TokenProvider tokenService;

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

  @PostMapping("/login")
  public ResponseEntity<JwtDto> signIn(@RequestBody @Valid SignInDto data) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
    var authUser = authenticationManager.authenticate(usernamePassword);
    var accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());
    return ResponseEntity.ok(new JwtDto(accessToken));
  }
}


