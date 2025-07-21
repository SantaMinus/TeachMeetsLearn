package com.sava.teachernet.controller;


import static com.sava.teachernet.config.auth.UserRole.ROLE_PENDING_OAUTH2_REGISTRATION;

import com.sava.teachernet.dto.RoleSelectionDto;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import com.sava.teachernet.service.AuthService;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2RegistrationController {

  private final UserRepository userRepository;
  private final AuthService authService;

  @GetMapping("/registration")
  public String showRoleSelectionForm(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();

    if (principal instanceof OAuth2User oauth2User) {
      String email = oauth2User.getAttribute("email");
      if (email == null) {
        email = oauth2User.getAttribute("login") + "@github.com";
      }

      User user = userRepository.findByLogin(email)
          .orElseThrow(
              () -> new IllegalStateException("User not found after OAuth2 authentication"));

      if (!Objects.equals(user.getRole(), ROLE_PENDING_OAUTH2_REGISTRATION.name())) {
        return "redirect:/";
      }

      model.addAttribute("roleSelection", new RoleSelectionDto());
      return "oauth2/select-role";
    }
    return "redirect:/";
  }

  @PostMapping("/select-role")
  public String processRoleSelection(
      @Valid @ModelAttribute("roleSelection") RoleSelectionDto roleSelection,
      BindingResult result) {

    if (result.hasErrors()) {
      return "oauth2/select-role";
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();

    if (principal instanceof OAuth2User oauth2User) {
      String email = oauth2User.getAttribute("email");
      if (email == null) {
        email = oauth2User.getAttribute("login") + "@github.com";
      }
      userRepository.findByLogin(email).ifPresent(user -> {
        user.setRole(roleSelection.getRole());
        userRepository.save(user);
        authService.refreshAuthentication();
      });
    }
    return "redirect:/";
  }
}
