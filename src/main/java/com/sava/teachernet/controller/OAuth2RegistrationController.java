package com.sava.teachernet.controller;

import static com.sava.teachernet.util.Constants.REDIRECT_HOME_URL;

import com.sava.teachernet.dto.RoleSelectionDto;
import com.sava.teachernet.service.OAuth2RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

  private final OAuth2RegistrationService oAuth2RegistrationService;

  @GetMapping("/registration")
  public String showRoleSelectionForm(Model model) {
    if (oAuth2RegistrationService.shouldShowRoleSelectionForm()) {
      model.addAttribute("roleSelection", new RoleSelectionDto());
      return "oauth2/select-role";
    }
    return REDIRECT_HOME_URL;
  }

  @PostMapping("/select-role")
  public String processRoleSelection(
      @Valid @ModelAttribute("roleSelection") RoleSelectionDto roleSelection,
      BindingResult result) {
    if (result.hasErrors()) {
      return "oauth2/select-role";
    }
    oAuth2RegistrationService.processRoleSelection(roleSelection);
    return REDIRECT_HOME_URL;
  }
}
