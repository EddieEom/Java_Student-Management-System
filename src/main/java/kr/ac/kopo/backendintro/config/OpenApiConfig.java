package kr.ac.kopo.backendintro.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "학생 관리 REST API",
        version = "1.0",
        description = """
Spring Boot 기반 학생 관리 서비스의 REST API 문서입니다.
학생 조회, 등록, 수정, 삭제, 검색, 정렬, 페이징 기능을 제공합니다.
"""

    )
)
public class OpenApiConfig {
}
