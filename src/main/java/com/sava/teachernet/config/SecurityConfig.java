package com.sava.teachernet.config;

import com.sava.teachernet.repository.UserRepository;
import com.sava.teachernet.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  public static final String LOGIN_PATH = "/auth/login";
  private final UserRepository userRepository;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers("/auth/**", "/login", "/oauth2/**", "/webjars/**",
                    "/css/**", "/js/**").permitAll()
                .requestMatchers("/students").permitAll()
                .requestMatchers("/teachers").permitAll()
                .requestMatchers("/teachers/search").permitAll()
                .requestMatchers("/students/**").hasRole("STUDENT")
                .requestMatchers("/teachers/**").hasRole("TEACHER")
                .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage(LOGIN_PATH)
            .loginProcessingUrl(LOGIN_PATH)
            .usernameParameter("login")
            .defaultSuccessUrl("/", true)
            .permitAll())
        .oauth2Login(oauth2 -> oauth2
            .loginPage(LOGIN_PATH)
            .defaultSuccessUrl("/", true)
            .userInfoEndpoint(userInfo -> userInfo
                .userService(oauth2UserService(userRepository)))
            .successHandler((_, response, _) -> response.sendRedirect("/")))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/auth/login?logout")
            .permitAll());
    return http.build();
  }

  @Bean
  public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(
      UserRepository userRepository) {
    return new CustomOAuth2UserService(userRepository);
  }

  @Bean
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
