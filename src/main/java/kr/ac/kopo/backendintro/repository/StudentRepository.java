package kr.ac.kopo.backendintro.repository;
import kr.ac.kopo.backendintro.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


// Student = 관리할 Entity, Long = Student 의 @Id 타입

public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findByNameContaining(String name, Pageable pageable);
    Page<Student> findByMajorContaining(String major, Pageable pageable);
    Page<Student> findByNameContainingAndMajorContaining(
            String name, String major, Pageable pageable);

}
