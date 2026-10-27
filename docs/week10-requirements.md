# Week 10 · REST API & JSON CRUD — PDF 요구사항 대조표

기준: `[강의]응용프로그래밍실습_10주(1).pdf` (35쪽), 특히 2·3·24~34쪽.

> 범례: **코드 구현**은 소스 코드에서 확인됨. **실행 검증**은 SQL Server를 연결하고 Postman의 실제 응답을 확인해야 완료됨. 두 상태를 혼동하지 않습니다.

| PDF 항목 | 요구사항 | 구현 위치 | 코드 확인 | 실행 검증 |
| --- | --- | --- | --- | --- |
| 실습 0 | 기존 /students CRUD·검색·정렬·페이징 유지 | `StudentController`, `StudentService` | 유지 | 로컬 DB 필요 |
| 실습 1 | Student JSON 목록·상세 | `StudentApiController` | 구현(기존 코드) | 로컬 DB 필요 |
| 실습 2 | Postman 200 + JSON 배열 | `postman/Week10-REST-CRUD.postman_collection.json` | 요청 정의 | Postman 실행 필요 |
| 실습 3 | POST + @RequestBody | `StudentApiController` | 구현(기존 코드) | 로컬 DB 필요 |
| 실습 4 | 201 Created, 상세 404 | `StudentApiController` | 구현(기존 코드) | 로컬 DB 필요 |
| 실습 5 | PUT 200·DELETE 204·404 | `StudentApiController`, `StudentService` | 구현 | DB/SQL 로그 필요 |
| 실습 6 | POST·PUT @Valid, 필드별 JSON 400 | `StudentApiController`, `ApiExceptionHandler` | PUT @Valid 보완 | Postman 실행 필요 |
| 실습 7 | name/major + 정렬 + 페이지 + 7단계 | `StudentService`, `StudentApiController`, Postman | 구현 | Postman 실행 필요 |
| 도전 1 | `CourseApiController`, `/api/courses` | `CourseApiController` | 구현 | 서버 시작 필요 |
| 도전 2 | GET 목록·상세 및 상세 404 | `CourseApiController` | 구현 | 로컬 DB 필요 |
| 도전 3 | POST 201 + 생성 id | `CourseApiController` | 구현 | 로컬 DB 필요 |
| 도전 4 | PUT 200 + Dirty Checking | `CourseApiController` → `CourseService.update` | 구현; save 호출 없음 | SQL UPDATE 확인 필요 |
| 도전 5 | DELETE 204, 재조회 404 | `CourseApiController` | 구현 | Postman 실행 필요 |
| 도전 6 | POST/PUT @Valid + 400 필드별 JSON | `Course`의 NotBlank/Size + 공유 `ApiExceptionHandler` | 구현 | Postman 실행 필요 |
| 도전 7 | 검색·정렬·페이징, Page JSON | `CourseService.search` 재사용 | 구현 | DB 데이터 여러 건 필요 |
| 도전 8 | Postman 7단계 생명주기 검증 | `postman/Week10-REST-CRUD.postman_collection.json` | 시나리오 구성 | 직접 실행 필요 |

## 설계 범위 및 PDF 준수 여부

- PDF 2쪽 규칙에 따라 **StudentService, StudentRepository, Student Entity, MVC 화면, pom.xml은 수정하지 않았습니다.** 기존 Student REST PUT 메서드에 누락됐던 `@Valid`를 추가했습니다.
- PDF 34쪽의 Course 도전과제는 전날에 만들어 둔 **CourseService/Repository/Entity를 수정하지 않고** REST Controller만 새로 추가했습니다.
- CourseService는 전날 과제에서 페이지 크기 **5 고정**으로 설계되었습니다. 따라서 Course API는 `page` 파라미터를 받고 `size`는 5이며, Student API와 달리 `size`를 선택하는 기능은 없습니다. PDF 도전과제는 별도의 가변 `size`를 필수로 명시하지 않습니다.
- `Course`에는 `@NotBlank`와 `@Size(max=100)`이 있으므로 빈 과정명·담당교수명을 제출하면 공통 핸들러에서 필드별 JSON을 반환합니다.
- `Page<Course>` 직접 직렬화는 프레임워크 버전에 따라 JSON 구조가 달라질 수 있습니다(PDF 30쪽). 이번 실습은 안내대로 Page를 그대로 반환합니다.

## 실행 확인 순서

1. SQL Server `localhost:1433`와 `BackendIntroDB` 준비. `DB_USERNAME`, `DB_PASSWORD` 등 환경변수 설정.
2. `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)로 기동, `/students`, `/courses` 화면 확인.
3. Postman에 컬렉션을 Import하고 `baseUrl=http://localhost:8080`으로 설정.
4. **Course: 01~07**을 순서대로 실행합니다. 01의 생성 응답에서 `courseId`가 자동 저장됩니다.
5. **Student: 01~07**도 실행하여 기존 REST 기능 회귀를 확인합니다.
6. Course PUT 이후 SQL Server에서 해당 `id`의 `name`·`teacher`가 바뀌었는지 확인하고 콘솔의 `update course ...`를 봅니다.
7. 실제 SQL Server 미접속 환경에서는 모든 테스트를 **미검증**으로 남겨야 합니다.

## Course 테스트용 SQL

```sql
SELECT * FROM course ORDER BY id;
SELECT * FROM student ORDER BY id;
```
