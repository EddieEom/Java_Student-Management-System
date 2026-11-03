package kr.ac.kopo.backendintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import kr.ac.kopo.backendintro.model.Student;
import kr.ac.kopo.backendintro.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // ★ 추가
import io.swagger.v3.oas.annotations.responses.ApiResponses; // ★ 추가


@Tag(
    name = "학생 관리 API",
    description = "학생 조회, 등록, 수정, 삭제 및 검색 API"
)
@RestController
@RequestMapping("/api/students")
public class StudentApiController {

    private final StudentService studentService;

    public StudentApiController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(
        summary = "학생 목록 조회",
        description = "이름과 학과 검색, 정렬, 페이징 조건을 이용하여 학생 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "학생 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<Page<Student>> students(
        @Parameter(description = "검색할 학생 이름의 일부", example = "김") // ★ 추가
        @RequestParam(required = false)
        String nameKeyword,
        @Parameter(description = "검색할 학과명의 일부", example = "소프트웨어") // ★ 추가
        @RequestParam(required = false)
        String majorKeyword,
        @Parameter(description = "정렬 기준: id, name, major, grade", example = "name") // ★ 추가
        @RequestParam(defaultValue = "id")
        String sortField,
        @Parameter(description = "정렬 방향: asc 또는 desc", example = "asc") // ★ 추가
        @RequestParam(defaultValue = "asc")
        String sortDir,
        @Parameter(description = "페이지 번호. 0부터 시작", example = "0") // ★ 추가
        @RequestParam(defaultValue = "0")
        int page,
        @Parameter(description = "한 페이지의 데이터 수", example = "5") // ★ 추가
        @RequestParam(defaultValue = "5")
        int size) {
        Page<Student> studentPage = studentService.search(
            nameKeyword,
            majorKeyword,
            sortField,
            sortDir,
            page,
            size
        );
        return ResponseEntity.ok(studentPage);
    }

    @Operation(summary = "학생 상세 조회",
        description = "학생 ID로 학생 한 명의 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "학생 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 학생을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Student> studentDetail(
        @Parameter(description = "조회할 학생의 ID", example = "1")
        @PathVariable Long id) {
        Student student = studentService.findById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "학생 정보 수정",
        description = "학생 ID에 해당하는 학생의 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "학생 정보 수정 성공"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 학생을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
        @Parameter(description = "수정할 학생의 ID", example = "1")
        @PathVariable Long id,
        @Valid @RequestBody Student student
    ) {
        Student updatedStudent = studentService.update(id, student);
        if (updatedStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedStudent);
    }

    @Operation(summary = "학생 등록",
        description = "이름, 학과, 학년 정보를 이용하여 새로운 학생을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "학생 등록 성공"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @Operation(summary = "학생 삭제",
        description = "학생 ID에 해당하는 학생 정보를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "학생 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 학생을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
        @Parameter(description = "삭제할 학생의 ID", example = "1")
        @PathVariable Long id) {
        Student student = studentService.findById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
