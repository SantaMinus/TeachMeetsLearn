package com.sava.teachernet.config.auth;

import static com.sava.teachernet.config.auth.CustomAuthenticationSuccessHandler.REDIRECT_OAUTH2_REGISTRATION;
import static com.sava.teachernet.config.auth.UserRole.ROLE_PENDING_OAUTH2_REGISTRATION;
import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.RedirectStrategy;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationSuccessHandlerTest {

  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private Authentication authentication;
  @Mock
  private RedirectStrategy redirectStrategy;

  private CustomAuthenticationSuccessHandler successHandler;

  @BeforeEach
  void setUp() {
    successHandler = new CustomAuthenticationSuccessHandler();
    successHandler.setRedirectStrategy(redirectStrategy);
  }

  @Test
  void onAuthenticationSuccess_whenPendingRegistration_thenRedirectsToRegistration()
      throws IOException, ServletException {
    when(authentication.getAuthorities())
        .thenReturn((Collection) List.of(
            new SimpleGrantedAuthority(ROLE_PENDING_OAUTH2_REGISTRATION.getValue())));

    successHandler.onAuthenticationSuccess(request, response, authentication);

    verify(redirectStrategy).sendRedirect(request, response, REDIRECT_OAUTH2_REGISTRATION);
  }

  @Test
  void onAuthenticationSuccess_whenNotPendingRegistration_thenUsesDefaultBehavior()
      throws IOException, ServletException {
    successHandler.setDefaultTargetUrl("/");
    when(authentication.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority(STUDENT.getValue())));

    successHandler.onAuthenticationSuccess(request, response, authentication);

    verify(redirectStrategy).sendRedirect(request, response, "/");
  }
}
