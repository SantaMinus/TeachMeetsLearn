package com.sava.teachernet.service;

import com.sava.teachernet.model.User;
import java.util.List;

public interface UserService<T extends User> {

  List<T> getAll();

  T getProfile(int id);

  T create(T student);

  T update(T student);

  void delete(T student);
}
