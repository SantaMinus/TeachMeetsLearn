package com.sava.teachernet.repository;

import com.sava.teachernet.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

//  Optional<Teacher> findByUserLogin(String login);
}
