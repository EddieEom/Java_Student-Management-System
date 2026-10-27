package kr.ac.kopo.backendintro.controller;
import jakarta.validation.Valid;
import kr.ac.kopo.backendintro.model.Student;
import kr.ac.kopo.backendintro.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")

public class StudentApiController {
    private final StudentService studentService;
    public StudentApiController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Page<Student>> students(
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) String majorKeyword,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Student> studentPage = studentService.search(
                nameKeyword, majorKeyword,
                sortField, sortDir, page, size);
        return ResponseEntity.ok(studentPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> studentDetail(
            @PathVariable Long id) {
        Student student = studentService.findById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody Student student) {
        Student updatedStudent = studentService.update(id, student);
        if (updatedStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedStudent);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(
            @Valid @RequestBody Student student) {
        Student savedStudent = studentService.save(student);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id) {
        Student student = studentService.findById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}