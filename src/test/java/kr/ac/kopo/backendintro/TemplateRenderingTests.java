package kr.ac.kopo.backendintro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import kr.ac.kopo.backendintro.controller.HelloController;
import kr.ac.kopo.backendintro.controller.StudentController;
import kr.ac.kopo.backendintro.model.Student;
import kr.ac.kopo.backendintro.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/** 화면 템플릿만 검증한다. SQL Server 저장 및 변경 감지 검증과는 별개다. */
class TemplateRenderingTests {

    private StudentService students;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        students = mock(StudentService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        var engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        var views = new ThymeleafViewResolver();
        views.setTemplateEngine(engine);
        views.setCharacterEncoding("UTF-8");
        mvc = MockMvcBuilders.standaloneSetup(
            new StudentController(students),
            new HelloController()
        )
            .setViewResolvers(views)
            .build();
    }

    @Test
    void emptyStudentListShowsNoticeInsteadOfTable() throws Exception {
        when(students.search(null, null, "id", "asc", 0, 5)).thenReturn(
            new PageImpl<>(List.of(), PageRequest.of(0, 5, Sort.by("id")), 0)
        );
        String html = mvc
            .perform(get("/students"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
        assertThat(html)
            .contains("조회된 학생이 없습니다.")
            .doesNotContain("<table", "학생 목록 페이지");
    }

    @Test
    void studentRowHasEditLinkAndConfirmedPostDelete() throws Exception {
        var student = new Student();
        student.setId(7L);
        student.setName("테스트 학생");
        student.setMajor("소프트웨어융합과");
        student.setGrade(2);
        when(students.search(null, null, "id", "asc", 0, 5)).thenReturn(
            new PageImpl<>(List.of(student), PageRequest.of(0, 5, Sort.by("id")), 1)
        );
        String html = mvc
            .perform(get("/students"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains(
            "href=\"/students/7/edit\"",
            "action=\"/students/7/delete\"",
            "method=\"post\"",
            "confirm(",
            "테스트 학생"
        );
        assertThat(html).doesNotContain("조회된 학생이 없습니다.");
    }

    @Test
    void helloResolvesTemplateWithMatchingFilenameCase() throws Exception {
        String html = mvc
            .perform(get("/hello").param("name", "유준"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains("유준");
    }
}
