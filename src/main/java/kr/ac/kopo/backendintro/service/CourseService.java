package kr.ac.kopo.backendintro.service;

import kr.ac.kopo.backendintro.model.Course;
import kr.ac.kopo.backendintro.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business rules and transaction boundary are kept out of the MVC controller. */
@Service
@Transactional
public class CourseService {
    private static final int PAGE_SIZE = 5;
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public Course update(Long id, Course changed) {
        Course managed = courseRepository.findById(id).orElse(null);
        if (managed == null) {
            return null;
        }
        managed.setName(changed.getName());
        managed.setTeacher(changed.getTeacher());
        // No save(): JPA dirty checking flushes changes when this transaction commits.
        return managed;
    }

    public void deleteById(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
        }
    }

    @Transactional(readOnly = true)
    public Page<Course> search(String nameKeyword, String teacherKeyword,
                               String sortField, String sortDir, int page) {
        boolean nameEmpty = nameKeyword == null || nameKeyword.isBlank();
        boolean teacherEmpty = teacherKeyword == null || teacherKeyword.isBlank();

        // Never interpolate a user-supplied column name into a Sort expression.
        String field = switch (sortField == null ? "" : sortField) {
            case "name" -> "name";
            case "teacher" -> "teacher";
            default -> "id";
        };
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(0, page), PAGE_SIZE,
                Sort.by(direction, field));

        if (nameEmpty && teacherEmpty) {
            return courseRepository.findAll(pageable);
        }
        if (!nameEmpty && teacherEmpty) {
            return courseRepository.findByNameContaining(nameKeyword, pageable);
        }
        if (nameEmpty) {
            return courseRepository.findByTeacherContaining(teacherKeyword, pageable);
        }
        return courseRepository.findByNameContainingAndTeacherContaining(
                nameKeyword, teacherKeyword, pageable);
    }
}
