package com.sava.teachernet.repository;

import com.sava.teachernet.model.Teacher;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>,
    JpaSpecificationExecutor<Teacher> {

  Optional<Teacher> findByUserLogin(String login);
}
