package kr.ac.kopo.backendintro.service;
import kr.ac.kopo.backendintro.model.Student;
import kr.ac.kopo.backendintro.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ★ 추가

import java.util.List;

// 의존성 주입(DI). 필요한 객체를 직접 new 로 만들지 않고 외부(Spring)에서 전달받음.

@Transactional // ★ 추가 — 클래스의 모든 메서드에 트랜잭션 적용
@Service
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElse(null);
    }
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public Student update(Long id, Student updatedStudent) {
        Student student = studentRepository.findById(id)
                .orElse(null);
        if (student == null) {
            return null;
        }
        student.setName(updatedStudent.getName());
        student.setMajor(updatedStudent.getMajor());
        student.setGrade(updatedStudent.getGrade());
        return student;
    }

    public Page<Student> search(String nameKeyword,
                                String majorKeyword,
                                String sortField, // ★ 추가
                                String sortDir, int page, int size) {

        boolean nameEmpty =
                nameKeyword == null || nameKeyword.isBlank();
        boolean majorEmpty =
                majorKeyword == null || majorKeyword.isBlank();
        String field; // ★ 여기부터 추가
        if ("name".equals(sortField)) {
            field = "name";
        } else if ("major".equals(sortField)) {
            field = "major";
        } else if ("grade".equals(sortField)) {
            field = "grade";
        } else {
            field = "id";
        }
        Sort.Direction direction;
        if ("desc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.ASC;
        }
        Sort sort = Sort.by(direction, field); // ★ 여기까지 추가
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        if (nameEmpty && majorEmpty) {
            return studentRepository.findAll(pageable);
        }
        if (!nameEmpty && majorEmpty) {
            return studentRepository.findByNameContaining(
                    nameKeyword, pageable);

        }
        if (nameEmpty) {
            return studentRepository.findByMajorContaining(
                    majorKeyword, pageable);

        }
        return studentRepository
                .findByNameContainingAndMajorContaining(
                        nameKeyword, majorKeyword, pageable);

    }
}
