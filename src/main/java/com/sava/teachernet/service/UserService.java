package com.sava.teachernet.service;

import com.sava.teachernet.model.AbstractUser;
import com.sava.teachernet.model.User;
import java.util.List;

public interface UserService<T extends AbstractUser> {

  List<T> getAll();

  T getProfile(String username);

  T create(String name, String lastName, User user);

  T update(T student);

  void delete(T student);
}
