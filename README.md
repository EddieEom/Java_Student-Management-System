# 학생·과정 관리 · Java Spring Boot CRUD

**Java·Spring Boot·Maven을 공부하며 학생과 교육과정 정보를 관리하는 웹과 REST API를 구현했습니다.**

Java 21 · Spring Boot · Maven · Spring Data JPA · MSSQL · Thymeleaf

<a href="docs/screenshots/student-list.png"><img src="docs/screenshots/student-list.png" alt="학생 목록과 검색 화면" width="100%"/></a>

## 구현한 기능

| 기능 | 적용 내용 |
| --- | --- |
| 학생·과정 등록·조회·수정·삭제 | Spring MVC, REST Controller, JPA |
| 조건 검색·정렬·페이징 | Repository 검색 메서드, Pageable |
| 입력 검증 | @Valid, 공통 예외 처리 |
| JSON API | GET·POST·PUT·DELETE와 상태코드 구분 |
| DB 연결 | SQL Server, 환경변수 기반 연결 설정 |

강의에서 제공된 학생 관리 소스를 바탕으로 확장한 실습입니다. 요구사항 정리와 AI를 활용한 코드 생성·수정, 로컬 DB 연결과 기능 확인을 진행했습니다.

## 개발하며 공부한 것

### 화면과 API가 같은 Service를 사용하도록 구성

Thymeleaf 화면과 JSON API는 응답 방식이 다르지만 데이터 처리는 같습니다. Controller → Service → Repository로 흐름을 나누고, MVC와 REST Controller가 같은 Service를 사용하도록 구성했습니다.

[CourseApiController](src/main/java/kr/ac/kopo/backendintro/controller/CourseApiController.java) · [CourseService](src/main/java/kr/ac/kopo/backendintro/service/CourseService.java)

### 저장됐다는 것 외에 확인할 응답

등록은 201, 삭제는 본문 없는 204, 없는 데이터는 404로 응답하도록 구성했습니다. 입력 검증을 확인하며 Student 수정 요청에 빠진 `@Valid`도 적용했습니다.

Course 수정에서는 트랜잭션 안에서 조회한 Entity의 필드를 바꾸고, JPA의 변경 감지로 반영되는 방식을 공부했습니다.

### DB 연결 오류에서 설정 확인

SQL Server 연결 중 로그인 이름이 `${DB_USERNAME}` 그대로 전달되는 오류가 있었습니다. SQL문보다 실행 환경변수 설정을 먼저 확인해야 하는 경우였습니다. DB 계정과 비밀번호는 환경변수로 공급하도록 구성했습니다.

## 실행 화면

<table><tr><td><a href="docs/screenshots/course-list.png"><img src="docs/screenshots/course-list.png" alt="과정 검색·정렬·페이징 화면" width="100%"/></a></td><td><a href="docs/screenshots/course-edit.png"><img src="docs/screenshots/course-edit.png" alt="교육과정 수정 화면" width="100%"/></a></td></tr></table>

로컬에서 실행한 화면입니다. [전체 화면 자료](docs/screenshots)에서 등록·상세 화면도 볼 수 있습니다.

## API와 확인 기록

- 학생 API: `/api/students`
- 과정 API: `/api/courses`
- [Postman 요청 모음](postman/Week10-REST-CRUD.postman_collection.json)
- [HTTP 스모크 32건 기록](docs/week10-http-smoke-results.json): 인메모리 Repository를 사용한 HTTP 검증
- SQL Server 기능별 동작은 로컬에서 확인했으며 위 HTTP 기록과 구분합니다.
- [실습 요구사항 정리](docs/week10-requirements.md)

## 실행 방법

Java 21과 SQL Server, `BackendIntroDB`를 준비합니다. `DB_USERNAME`·`DB_PASSWORD`를 실행 환경변수에 설정하고 필요하면 `DB_URL`도 변경합니다. [설정 예제](.env.example)

```powershell
.\mvnw.cmd spring-boot:run
```

macOS·Linux에서는 `./mvnw spring-boot:run`을 사용합니다. 실행 후 `http://localhost:8080/students` 또는 `http://localhost:8080/courses`에서 확인합니다.

[프로젝트 소개 문서](docs/index.html)
