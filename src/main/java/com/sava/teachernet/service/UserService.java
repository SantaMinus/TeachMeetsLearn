package com.sava.teachernet.service;

import com.sava.teachernet.model.AbstractUser;
import java.util.List;

public interface UserService<T extends AbstractUser> {

  List<T> getAll();

  T getProfile(int id);

  T create(T student);

  T update(T student);

  void delete(T student);
}
