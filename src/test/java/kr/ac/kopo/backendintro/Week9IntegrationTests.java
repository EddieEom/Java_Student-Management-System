package kr.ac.kopo.backendintro;

import jakarta.persistence.EntityManager;
import kr.ac.kopo.backendintro.model.Student;
import kr.ac.kopo.backendintro.repository.StudentRepository;
import kr.ac.kopo.backendintro.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 실제 설정된 SQL Server를 사용한다. 각 테스트의 데이터 변경은 롤백된다. */
@SpringBootTest
@Transactional
class Week9IntegrationTests {
    @Autowired StudentService students;
    @Autowired StudentRepository studentRepository;
    @Autowired EntityManager entityManager;
    @Autowired WebApplicationContext context;
    MockMvc mvc;
    String token;
    Long studentId;

    @BeforeEach
    void fixtures() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        token = "W9" + UUID.randomUUID().toString().substring(0, 8);
        for (int i = 0; i < 8; i++) {
            Student student = new Student();
            student.setName(token + (i < 7 ? "길" : "다른") + i);
            student.setMajor(token + (i < 6 || i == 7 ? "소프트웨어" : "기계"));
            student.setGrade(i % 2 + 1);
            studentId = students.save(student).getId();
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void fourSearchBranchesAndPageTotals() {
        assertThat(students.search(null, " ", "id", "asc", 0, 5).getTotalElements())
                .isEqualTo(studentRepository.count());
        assertThat(students.search(token + "길", null, "id", "asc", 0, 5).getTotalElements()).isEqualTo(7);
        assertThat(students.search(null, token + "소프트웨어", "id", "asc", 0, 5).getTotalElements()).isEqualTo(7);
        var result = students.search(token + "길", token + "소프트웨어", "name", "asc", 1, 5);
        assertThat(result.getTotalElements()).isEqualTo(6);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo(token + "길5");

    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "name", "major", "grade"})
    void studentSortDirections(String field) {
        var ascending = students.search(token, null, field, "asc", 0, 20).getContent();
        var descending = students.search(token, null, field, "desc", 0, 20).getContent();
        assertThat(ascending).extracting(field).isSorted();
        assertThat(descending.reversed()).extracting(field).isSorted();
    }

    @Test
    void invalidSortAndPageValuesAreSafe() throws Exception {
        assertThat(students.search(token, null, "abcd", "bad", -1, 0).getSize()).isEqualTo(1);
        mvc.perform(get("/students").param("sortField", "abcd").param("page", "-1").param("size", "0"))
                .andExpect(status().isOk()).andExpect(model().attribute("sortField", "id"))
                .andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("size", 1));
    }

    @Test
    void studentNavigationRetainsAllConditions() throws Exception {
        String html = mvc.perform(get("/students").param("nameKeyword", token + "길")
                        .param("majorKeyword", token + "소프트웨어").param("sortField", "name")
                        .param("sortDir", "desc").param("size", "5"))
                .andExpect(status().isOk()).andExpect(model().attribute("totalItems", 6L))
                .andExpect(content().string(containsString("name=\"page\" value=\"0\"")))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        String next = linkForPageOne(html, "/students");
        assertThat(next).contains("size=5", "nameKeyword=", "majorKeyword=", "sortField=name", "sortDir=desc");
        mvc.perform(get(java.net.URI.create(next))).andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 1)).andExpect(model().attribute("totalItems", 6L))
                .andExpect(content().string(containsString(token + "길0")));
    }

    private String linkForPageOne(String html, String path) {
        var matcher = java.util.regex.Pattern.compile("href=\"(" + path + "\\?page=1[^\"]*)\"").matcher(html);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1).replace("&amp;", "&");
    }

    @Test
    void emptyResultsHaveNoPageNumbers() throws Exception {
        for (String path : new String[]{"/students"}) {
            mvc.perform(get(path).param("nameKeyword", token + "missing"))
                    .andExpect(status().isOk()).andExpect(model().attribute("totalPages", 0))
                    .andExpect(content().string(org.hamcrest.Matchers.not(containsString("class=\"btn primary\" aria-current=\"page\""))));
        }
    }

    @Test
    void studentCrudAndValidation() throws Exception {
        mvc.perform(get("/student-form")).andExpect(status().isOk());
        mvc.perform(get("/students/{id}", studentId)).andExpect(status().isOk());
        mvc.perform(get("/students/{id}/edit", studentId)).andExpect(status().isOk());
        mvc.perform(post("/student").param("name", " ").param("major", " ").param("grade", "3"))
                .andExpect(model().attributeHasFieldErrors("student", "name", "major", "grade"));
        long before = studentRepository.count();
        mvc.perform(post("/student").param("name", token + "등록").param("major", token).param("grade", "1"))
                .andExpect(redirectedUrl("/students"));
        assertThat(studentRepository.count()).isEqualTo(before + 1);
        mvc.perform(post("/students/{id}/edit", studentId).param("name", "").param("major", token).param("grade", "1"))
                .andExpect(view().name("student-edit")).andExpect(model().attributeHasFieldErrors("student", "name"))
                .andExpect(content().string(containsString("/students/" + studentId + "/edit")));
        mvc.perform(post("/students/{id}/edit", studentId).param("name", token + "변경").param("major", token).param("grade", "2"))
                .andExpect(redirectedUrl("/students"));
        entityManager.flush();
        entityManager.clear();
        assertThat(students.findById(studentId).getName()).isEqualTo(token + "변경");
        assertThat(students.search(token + "변경", null, "id", "asc", 0, 5).getTotalElements()).isEqualTo(1);
        mvc.perform(post("/students/{id}/delete", studentId)).andExpect(redirectedUrl("/students"));
        entityManager.flush();
        assertThat(students.findById(studentId)).isNull();
    }

    @Test
    void missingRecordsRedirectWithoutCreatingRows() throws Exception {
        mvc.perform(get("/students/{id}", Long.MAX_VALUE)).andExpect(redirectedUrl("/students"));
        mvc.perform(get("/students/{id}/edit", Long.MAX_VALUE)).andExpect(redirectedUrl("/students"));
        assertThat(students.update(Long.MAX_VALUE, new Student())).isNull();
    }
}
