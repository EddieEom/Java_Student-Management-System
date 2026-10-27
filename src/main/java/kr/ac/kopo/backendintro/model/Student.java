package kr.ac.kopo.backendintro.model; // 패키지명은 소문자 관례
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;
    @NotBlank(message = "학과를 입력하세요.")
    private String major;
    @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
    @Max(value = 2, message = "학년은 2 이하여야 합니다.")

    private int grade;



    // 기본 생성자
    public Student() {
    }

    // Getter & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}