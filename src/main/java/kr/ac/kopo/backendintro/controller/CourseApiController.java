package kr.ac.kopo.backendintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(
    name = "과목 관리 API",
    description = "과목 조회, 등록, 수정, 삭제 및 검색 API"
)
@RestController
@RequestMapping("/api/courses")
public class CourseApiController {

    private final CourseService courseService;

    public CourseApiController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "과목 목록 조회", description = "과목명과 담당교수로 검색하고 정렬한 결과를 페이지당 5개씩 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "과목 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<Page<Course>> courses(
        @Parameter(description = "검색할 과목명의 일부", example = "프로그래밍")
        @RequestParam(required = false) String nameKeyword,
        @Parameter(description = "검색할 담당교수 이름의 일부", example = "김")
        @RequestParam(required = false) String teacherKeyword,
        @Parameter(description = "정렬 기준 (id, name, teacher)", example = "name")
        @RequestParam(defaultValue = "id") String sortField,
        @Parameter(description = "정렬 방향 (asc, desc)", example = "asc")
        @RequestParam(defaultValue = "asc") String sortDir,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
        @RequestParam(defaultValue = "0") int page
    ) {
        Page<Course> result = courseService.search(
            nameKeyword,
            teacherKeyword,
            sortField,
            sortDir,
            page
        );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "과목 상세 조회", description = "ID로 과목명과 담당교수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "과목 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 과목이 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Course> courseDetail(
        @Parameter(description = "과목 ID", example = "1")
        @PathVariable Long id
    ) {
        Course course = courseService.findById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "과목 등록", description = "과목명과 담당교수를 등록하고 생성된 과목을 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "과목 등록 성공"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        // New resources must receive their identity from SQL Server, not from client JSON.
        course.setId(null);
        Course savedCourse = courseService.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @Operation(summary = "과목 수정", description = "ID에 해당하는 과목의 과목명과 담당교수를 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "과목 수정 성공"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 과목이 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
        @Parameter(description = "과목 ID", example = "1")
        @PathVariable Long id,
        @Valid @RequestBody Course course
    ) {
        // The existing transactional CourseService uses dirty checking (no save call).
        Course updatedCourse = courseService.update(id, course);
        if (updatedCourse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCourse);
    }

    @Operation(summary = "과목 삭제", description = "ID에 해당하는 과목을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "과목 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "해당 ID의 과목이 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
        @Parameter(description = "과목 ID", example = "1")
        @PathVariable Long id
    ) {
        if (courseService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
