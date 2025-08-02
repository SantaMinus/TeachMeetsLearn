package com.sava.teachernet.service;

import com.google.common.annotations.VisibleForTesting;
import com.sava.teachernet.config.auth.UserRole;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);
    return processOAuth2User(oauth2User);
  }

  @VisibleForTesting
  OAuth2User processOAuth2User(OAuth2User oauth2User) {
    Map<String, Object> attributes = oauth2User.getAttributes();

    String username = attributes.get("login").toString();

    User user = userRepository.findByLogin(username).orElseGet(() -> {
      User newUser = new User();
      newUser.setLogin(username);
      newUser.setRole(UserRole.ROLE_PENDING_OAUTH2_REGISTRATION.name());
      // TODO: require a user to change password
      newUser.setPassword("{noop}oauth2user" + System.currentTimeMillis());
      return userRepository.save(newUser);
    });

    List<GrantedAuthority> authorities =
        user.getRole().equals(UserRole.ROLE_PENDING_OAUTH2_REGISTRATION.name())
            ? List.of(new SimpleGrantedAuthority(UserRole.ROLE_PENDING_OAUTH2_REGISTRATION.name()))
            : List.of(new SimpleGrantedAuthority(user.getRole()));

    Authentication authentication = new UsernamePasswordAuthenticationToken(
        oauth2User, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    return new DefaultOAuth2User(authorities, attributes, "login");
  }
}
