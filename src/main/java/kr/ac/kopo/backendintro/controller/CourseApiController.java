package kr.ac.kopo.backendintro.controller;

import jakarta.validation.Valid;
import kr.ac.kopo.backendintro.model.Course;
import kr.ac.kopo.backendintro.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * JSON presentation layer for the existing CourseService.
 * Shares the same data and business rules with the Thymeleaf CourseController.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseApiController {
    private final CourseService courseService;

    public CourseApiController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * CourseService from the previous lab fixes the page size at five.
     * The API intentionally retains that service contract unchanged.
     */
    @GetMapping
    public ResponseEntity<Page<Course>> courses(
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) String teacherKeyword,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page) {
        Page<Course> result = courseService.search(
                nameKeyword, teacherKeyword, sortField, sortDir, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> courseDetail(@PathVariable Long id) {
        Course course = courseService.findById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        // New resources must receive their identity from SQL Server, not from client JSON.
        course.setId(null);
        Course savedCourse = courseService.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody Course course) {
        // The existing transactional CourseService uses dirty checking (no save call).
        Course updatedCourse = courseService.update(id, course);
        if (updatedCourse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (courseService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
