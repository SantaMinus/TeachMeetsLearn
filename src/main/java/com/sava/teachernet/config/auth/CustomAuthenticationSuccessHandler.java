package com.sava.teachernet.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

  public static final String REDIRECT_OAUTH2_REGISTRATION = "/oauth2/registration";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {
    boolean isPendingRegistration = authentication.getAuthorities()
        .contains(
            new SimpleGrantedAuthority(UserRole.ROLE_PENDING_OAUTH2_REGISTRATION.getValue()));

    if (isPendingRegistration) {
      getRedirectStrategy().sendRedirect(request, response, REDIRECT_OAUTH2_REGISTRATION);
    } else {
      super.onAuthenticationSuccess(request, response, authentication);
    }
  }
}
