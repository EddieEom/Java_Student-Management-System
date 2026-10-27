package kr.ac.kopo.backendintro.controller;
import kr.ac.kopo.backendintro.model.Student;
import kr.ac.kopo.backendintro.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import org.springframework.data.domain.Page;


@Controller
public class StudentController {

    private final StudentService studentService;
    public StudentController(StudentService studentService) {

        this.studentService = studentService;
    }



    @GetMapping("/student-form")
    public String studentForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }
    @PostMapping("/student")
    public String student(
            @Valid @ModelAttribute Student student,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "student-form";
        }
        studentService.save(student);
        return "redirect:/students";
    }

    @GetMapping("/students")
    public String students(
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) String majorKeyword,
            @RequestParam(defaultValue = "id") String sortField, // ★ 추가
            @RequestParam(defaultValue = "asc") String sortDir, // ★ 추가
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Page<Student> studentPage = studentService.search(
                nameKeyword, majorKeyword,
                sortField, sortDir, page, size);
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", studentPage.getNumber());
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());
        model.addAttribute("size", studentPage.getSize());
        model.addAttribute("nameKeyword", nameKeyword);
        model.addAttribute("majorKeyword", majorKeyword);
        var order = studentPage.getSort().iterator().next();
        model.addAttribute("sortField", order.getProperty());
        model.addAttribute("sortDir", order.isDescending() ? "desc" : "asc");
        return "students";
    }

    @GetMapping("/students/{id}")
    public String studentDetail(
            @PathVariable Long id,
            Model model) {
        Student student = studentService.findById(id); // ★ Service 호출로 변경
        if (student == null) {
            return "redirect:/students";
        }
        model.addAttribute("student", student);
        return "student-detail";
    }

    @GetMapping("/students/{id}/edit")
    public String studentEditForm(
            @PathVariable Long id,
            Model model) {

        Student student = studentService.findById(id);

        if (student == null) {
            return "redirect:/students";
        }

        model.addAttribute("student", student);
        return "student-edit";
    }

    @PostMapping("/students/{id}/edit")
    public String studentEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute Student student,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            student.setId(id); // 화면 재표시용 — th:action 에 필요
            return "student-edit";
        }
        studentService.update(id, student); // ★ save() 가 아닙니다
        return "redirect:/students";
    }

    @PostMapping("/students/{id}/delete")
    public String studentDelete(@PathVariable Long id) {
        studentService.deleteById(id);
        return "redirect:/students";
    }

}
