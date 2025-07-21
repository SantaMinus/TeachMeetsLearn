package com.sava.teachernet.service;

import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);
    Map<String, Object> attributes = oauth2User.getAttributes();

    String email = (String) attributes.get("email");
    if (email == null) {
      email = oauth2User.getAttribute("login") + "@github.com";
    }

    Optional<User> existingUser = userRepository.findByLogin(email);

    if (existingUser.isEmpty()) {
      User newUser = new User();
      newUser.setLogin(email);
      // TODO: require a user to specify a role
      newUser.setRole("ROLE_STUDENT");
      // TODO: require a user to change password
      newUser.setPassword("{noop}oauth2user" + System.currentTimeMillis());
      userRepository.save(newUser);
    }

    return new DefaultOAuth2User(
        List.of(() -> "ROLE_STUDENT"), attributes, "login");
  }
}
