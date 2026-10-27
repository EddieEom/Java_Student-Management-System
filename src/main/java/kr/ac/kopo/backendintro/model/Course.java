package kr.ac.kopo.backendintro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Course record for the optional week-9 extension exercise. */
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "과정명을 입력해 주세요.")
    @Size(max = 100, message = "과정명은 100자 이내로 입력해 주세요.")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "담당교수를 입력해 주세요.")
    @Size(max = 100, message = "담당교수는 100자 이내로 입력해 주세요.")
    @Column(nullable = false, length = 100)
    private String teacher;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
}
