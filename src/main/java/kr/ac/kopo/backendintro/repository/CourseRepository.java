package kr.ac.kopo.backendintro.repository;

import kr.ac.kopo.backendintro.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByNameContaining(String name, Pageable pageable);
    Page<Course> findByTeacherContaining(String teacher, Pageable pageable);
    Page<Course> findByNameContainingAndTeacherContaining(
            String name, String teacher, Pageable pageable);
}
