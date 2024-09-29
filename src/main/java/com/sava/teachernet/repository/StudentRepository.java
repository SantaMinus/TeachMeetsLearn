package com.sava.teachernet.repository;

import com.sava.teachernet.model.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

  Optional<Student> findByUserLogin(String login);
}
