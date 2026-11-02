# 7~10주차 PDF 요구사항 대조

첨부된 응용프로그래밍실습 7·8·9·10주 PDF를 현재 프로젝트의 최종 형태와 비교했다.
각 주차의 중간 버전을 모두 보존했다는 의미는 아니다. 7주차 메모리 저장소는
8주차에서 JPA로 교체되고, 9주차부터 Controller가 Service를 통해 데이터에 접근한다.

## 코드에서 확인한 항목

| 자료 | 요구사항 | 현재 구현 |
| --- | --- | --- |
| 7주차 실습 1~6 | 학생 ID, 생성자 DI, 등록·목록·상세·수정·삭제, PRG | `Student`, `StudentController`, `StudentService`, 학생 화면 4종 |
| 7주차 20쪽 도전 1 | 학생 목록에서 바로 수정·삭제 | 각 행의 관리 열에서 수정 링크와 POST 삭제 폼 제공, 삭제 전 확인 |
| 7주차 20쪽 도전 2 | 학생 수 표시 | 페이징된 현재 행 수가 아닌 `totalItems`로 검색 결과 전체 건수 표시 |
| 7주차 20쪽 도전 3 | 빈 목록은 표 대신 안내 | `th:unless`로 빈 표를 숨기고 `th:if`로 안내 표시. 이번 검토에서 보완 |
| 8주차 실습 1~2 | SQL Server 연결, JPA Entity, IDENTITY, JpaRepository | `application.properties`, `pom.xml`, `Student`, `StudentRepository` |
| 8주차 실습 3~7 | DB CRUD, 없는 ID 처리, Validation, 영속 저장 | 학생 CRUD, Optional 처리, 이름·학과 필수 및 학년 1~2 검증 구현. 재시작 영속성은 DB 실행 확인 필요 |
| 8주차 20쪽 도전 | Course Entity·Repository, 등록·목록, 선택 Validation·수정·삭제 | `Course`, `CourseRepository`, `CourseController`, 과정 화면 3종 |
| 9주차 실습 1~3 | Service 분리, 트랜잭션, Dirty Checking | 학생·과정 Service에 생성자 DI와 `@Transactional`, 수정 메서드는 조회한 Entity 변경 후 반환 |
| 9주차 실습 4~6 | 부분·AND 검색, 정렬 whitelist, Page·Pageable | 학생 이름/학과, 과정명/담당교수 검색. 허용 정렬 필드 제한. 기본 페이지 크기 5 |
| 9주차 실습 7 | 이전·번호·다음, 조건 유지, 새 검색 첫 페이지 | 학생·과정 목록에서 검색·정렬 파라미터 유지. 화면 페이지는 내부 번호 +1 |
| 9주차 32쪽 도전 | Course에도 계층 분리·검색·정렬·5건 페이징 | `CourseService`, `CourseRepository`, `CourseController`, `courses.html` |
| 10주차 실습 1~5 | REST 조회·등록·수정·삭제, JSON, 200·201·204·404 | `/api/students`, `StudentApiController`, 기존 `StudentService` 재사용 |
| 10주차 실습 6 | POST·PUT 검증 및 필드별 400 JSON | 두 요청에 `@Valid`, 공통 `ApiExceptionHandler` |
| 10주차 실습 7 | REST 검색·정렬·페이징, 7단계 검증 시나리오 | 학생 목록 API, Postman 컬렉션. 최초 단순 JSON 배열은 최종 `Page` 응답으로 교체된 상태 |
| 10주차 34쪽 도전 | Course REST와 상태코드·검증·검색·정렬·페이징 | `/api/courses`, `CourseApiController`, 기존 `CourseService` 재사용. 크기는 5 고정 |

## 이번 정리에서 바뀐 내용

- Java 13개, 기존 테스트 2개, HTML 템플릿 9개, Course CSS의 들여쓰기와 줄바꿈을 정리했다.
- `.editorconfig`로 UTF-8·공백 4칸·LF 줄바꿈을 지정했다. IntelliJ에서도 같은 들여쓰기 기준을 적용할 수 있다.
- 빈 학생 목록에서 표가 함께 나타나던 부분을 보완했다.
- `/hello`가 반환하는 `hello`와 실제 템플릿 파일의 대소문자를 일치시켰다.
- Service·Repository의 역할과 SQL Server 연결 설정은 유지했다.

## 검증 범위

이번 검토에서 JDK 21로 전체 Java 소스와 테스트를 컴파일했고, 아래 화면 테스트
3개는 실패·오류 없이 통과했다. Java·HTML·CSS 포맷 검사와 `git diff --check`도 통과했다.

`TemplateRenderingTests`는 실제 Thymeleaf 템플릿을 렌더링해 빈 목록의 표 숨김,
학생 행의 수정·POST 삭제 경로와 확인창, `/hello` 템플릿 로딩을 검사한다.
이 테스트는 Service를 대체하므로 SQL Server 저장과 Dirty Checking을 검증하지 않는다.

```powershell
# JDK 21에서 화면 테스트만 실행 (SQL Server 불필요)
.\mvnw.cmd -Dtest=TemplateRenderingTests test

# SQL Server 환경변수를 설정한 뒤 기존 DB 통합 테스트까지 실행
.\mvnw.cmd test
```

DB 기동·SSMS 조회·서버 재시작 전후 데이터 유지·Postman의 실제 SQL Server 응답은
현재 검토 환경에서 확인하지 않았다. 기존 `week10-http-smoke-results.json`의
32건 기록은 인메모리 Repository 기반의 이전 HTTP 검증 기록이며 이번 실행 결과가 아니다.
