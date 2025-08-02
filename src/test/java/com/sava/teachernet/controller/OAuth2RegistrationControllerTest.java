package com.sava.teachernet.controller;

import static com.sava.teachernet.util.Constants.REDIRECT_HOME_URL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sava.teachernet.config.auth.UserRole;
import com.sava.teachernet.dto.RoleSelectionDto;
import com.sava.teachernet.service.OAuth2RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OAuth2RegistrationController.class)
class OAuth2RegistrationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OAuth2RegistrationService oAuth2RegistrationService;

  @Test
  @WithMockUser
  void showRoleSelectionFormReturnsSelectRoleView() throws Exception {
    when(oAuth2RegistrationService.shouldShowRoleSelectionForm()).thenReturn(true);

    mockMvc.perform(get("/oauth2/registration")
            .with(oauth2Login()))
        .andExpect(status().isOk())
        .andExpect(view().name("oauth2/select-role"))
        .andExpect(model().attributeExists("roleSelection"));
  }

  @Test
  @WithMockUser
  void showRoleSelectionFormRedirectsHome() throws Exception {
    when(oAuth2RegistrationService.shouldShowRoleSelectionForm()).thenReturn(false);

    mockMvc.perform(get("/oauth2/registration")
            .with(oauth2Login()))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(REDIRECT_HOME_URL));
  }

  @Test
  @WithMockUser
  void processRoleSelectionProcessesAndRedirects() throws Exception {
    RoleSelectionDto roleSelection = new RoleSelectionDto();
    roleSelection.setRole(UserRole.STUDENT.getValue());

    mockMvc.perform(post("/oauth2/select-role")
            .with(oauth2Login())
            .with(csrf())
            .flashAttr("roleSelection", roleSelection))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(REDIRECT_HOME_URL));

    verify(oAuth2RegistrationService).processRoleSelection(roleSelection);
  }

  @Test
  @WithMockUser
  void processRoleSelectionReturnsSelectRoleViewOnInvalidInput() throws Exception {
    RoleSelectionDto roleSelection = new RoleSelectionDto();
    roleSelection.setRole(null);

    mockMvc.perform(post("/oauth2/select-role")
            .with(oauth2Login())
            .with(csrf())
            .flashAttr("roleSelection", roleSelection))
        .andExpect(status().isOk())
        .andExpect(view().name("oauth2/select-role"));
  }
}
