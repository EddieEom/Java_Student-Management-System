package kr.ac.kopo.backendintro.controller;

import jakarta.validation.Valid;
import kr.ac.kopo.backendintro.model.Course;
import kr.ac.kopo.backendintro.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public String courses(@RequestParam(required = false) String nameKeyword,
                          @RequestParam(required = false) String teacherKeyword,
                          @RequestParam(defaultValue = "id") String sortField,
                          @RequestParam(defaultValue = "asc") String sortDir,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<Course> result = courseService.search(
                nameKeyword, teacherKeyword, sortField, sortDir, page);
        Sort.Order appliedOrder = result.getSort().iterator().next();
        model.addAttribute("courses", result.getContent());
        model.addAttribute("currentPage", result.getNumber());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalItems", result.getTotalElements());
        model.addAttribute("nameKeyword", nameKeyword);
        model.addAttribute("teacherKeyword", teacherKeyword);
        model.addAttribute("sortField", appliedOrder.getProperty());
        model.addAttribute("sortDir", appliedOrder.isDescending() ? "desc" : "asc");
        return "courses";
    }

    @GetMapping("/courses/new")
    public String newCourse(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("editing", false);
        return "course-form";
    }

    @PostMapping("/courses")
    public String createCourse(@Valid @ModelAttribute("course") Course course,
                               BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("editing", false);
            return "course-form";
        }
        courseService.save(course);
        return "redirect:/courses";
    }

    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }
        model.addAttribute("course", course);
        return "course-detail";
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id);
        if (course == null) {
            return "redirect:/courses";
        }
        model.addAttribute("course", course);
        model.addAttribute("editing", true);
        return "course-form";
    }

    @PostMapping("/courses/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute("course") Course course,
                               BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            course.setId(id); // Keep the edit form action valid after validation errors.
            model.addAttribute("editing", true);
            return "course-form";
        }
        if (courseService.update(id, course) == null) {
            return "redirect:/courses";
        }
        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteById(id);
        return "redirect:/courses";
    }
}
