package com.sava.teachernet.controller.student;

import com.sava.teachernet.dto.SearchDto;
import com.sava.teachernet.dto.StudentDto;
import com.sava.teachernet.dto.TeacherShortDto;
import com.sava.teachernet.service.StudentService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;

  @GetMapping
  public String getStudents(Model model) {
    List<StudentDto> studentList = studentService.getAll();
    model.addAttribute("studentList", studentList);

    return "students";
  }

  @GetMapping("/me/dashboard")
  public String showCurrentStudentDashboard() {
    return "student/dashboard";
  }

  @GetMapping("/me/profile")
  public String showCurrentStudentProfile(Model model) {
    StudentDto student = studentService.getCurrentStudentProfile();

    model.addAttribute("student", student);
    return "student/profile";
  }

  @GetMapping("/me/teachers")
  public String getCurrentStudentTeachers(Model model) {
    Set<TeacherShortDto> studentTeachers = studentService.getCurrentStudentProfile().getTeachers();

    model.addAttribute("teacherList", studentTeachers);
    model.addAttribute("searchDto", new SearchDto());
    return "student/teachers";
  }

  @PostMapping("/me/teachers")
  public String assignTeacherToCurrentStudent(@RequestParam Long teacherId) {
    studentService.assignTeacherToCurrentStudent(teacherId);
    return "redirect:/students/me/teachers";
  }

  @DeleteMapping("/me/teachers")
  public String unassignTeacherFromCurrentStudent(@RequestParam Long teacherId) {
    studentService.unassignTeacherFromCurrentStudent(teacherId);
    return "redirect:/students/me/teachers";
  }
}
