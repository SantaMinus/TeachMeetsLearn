package com.sava.teachernet.service;

import static com.sava.teachernet.config.auth.UserRole.STUDENT;
import static com.sava.teachernet.config.auth.UserRole.TEACHER;

import com.sava.teachernet.dto.SignUpDto;
import com.sava.teachernet.exception.InvalidAuthException;
import com.sava.teachernet.model.Student;
import com.sava.teachernet.model.Teacher;
import com.sava.teachernet.model.User;
import com.sava.teachernet.repository.StudentRepository;
import com.sava.teachernet.repository.TeacherRepository;
import com.sava.teachernet.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;

  @Override
  public UserDetails loadUserByUsername(String login) {
    User user = userRepository.findByLogin(login)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
    return new org.springframework.security.core.userdetails.User(user.getLogin(),
        user.getPassword(), List.of(authority));
  }

  public void signUp(SignUpDto data) throws InvalidAuthException {
    if (data.role() == null) {
      throw new InvalidAuthException("Role is not specified");
    }
    if (userRepository.findByLogin(data.login()).isPresent()) {
      throw new InvalidAuthException("Username already exists");
    }
    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    User newUser = new User(data.login(), encryptedPassword, data.role().getValue());
    userRepository.save(newUser);

    if (Objects.equals(data.role(), STUDENT)) {
      Student student = Student.builder()
          .name(data.name())
          .lastName(data.lastName())
          .user(newUser)
          .dateJoined(LocalDate.now())
          .build();
      studentRepository.save(student);
    } else if (Objects.equals(data.role(), TEACHER)) {
      Teacher teacher = Teacher.builder()
          .name(data.name())
          .lastName(data.lastName())
          .user(newUser)
          .dateJoined(LocalDate.now())
          .build();
      teacherRepository.save(teacher);
    }
  }
}
