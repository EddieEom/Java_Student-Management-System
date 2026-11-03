# 9주 PDF 요구사항 구현 및 검증

대상: `[강의]응용프로그래밍실습_9주.pdf`의 실습 0~7. 사용자 요청에 따라 32쪽 도전과제는 제외했다. 기존 Spring Boot/MSSQL 의존성과 DB 접속 설정은 변경하지 않았다.

| PDF | 요구사항 | 구현 위치 | 검증 |
| --- | --- | --- | --- |
| 4쪽, 실습 0 | 기존 CRUD와 8명 이상의 페이징 데이터 | 기존 Student CRUD 유지 | 각 통합 테스트에서 학생 8명 생성 후 롤백 |
| 5~10쪽, 실습 1~2 | 생성자 DI, Controller → Service → Repository | StudentController, StudentService | Controller의 Repository 참조 0건, 등록·상세·수정·삭제 요청 검증 |
| 11~13쪽, 실습 3 | Spring @Transactional, save 없는 수정 | StudentService.update | SQL Server에서 flush 후 영속성 컨텍스트를 비우고 변경값 재조회 |
| 14~18쪽, 실습 4 | 전체·이름·학과·AND 검색 | StudentRepository, StudentService.search | 네 가지 분기와 검색 건수 검증 |
| 19~23쪽, 실습 5 | id/name/major/grade whitelist, ASC/DESC | StudentService.search | 모든 허용 필드의 양방향 정렬, 잘못된 필드의 ID 대체 검증 |
| 24~27쪽, 실습 6 | Sort 버전을 Pageable/Page로 교체, 기본 5건 | StudentRepository, StudentService, StudentController | 기존 List/Sort 검색 오버로드 제거, 두 번째 페이지와 전체 건수 검증 |
| 28~31쪽, 실습 7 | 이전·번호·다음, 현재 페이지 강조, 화면 번호 +1 | students.html | 실제 렌더링된 다음 페이지 링크로 요청해 조건과 결과 유지 확인 |
| 28쪽 | 새 검색 page=0, size 유지 | students.html 검색 폼 | hidden 필드 렌더링과 페이지 크기 전달 검증 |

## 실행

JDK 21과 SQL Server를 실행한 상태에서 프로젝트 루트에서 실행한다.

```powershell
$env:JAVA_HOME = 'D:/2026_JAVA/TOOLS/jdk-21.0.12.1+1'
./mvnw.cmd spring-boot:run
```

- 학생 관리: http://localhost:8080/students
- 학생은 기본 5건이며 `size`로 크기를 지정할 수 있다.
- 빈 결과에서는 페이지 번호를 표시하지 않는다. 음수 page는 0, 학생의 0 이하 size는 1로 보정한다.
- `application.properties`의 기존 SQL Server 연결 설정과 `ddl-auto=update`를 사용한다.

## 자동 검증

```powershell
./mvnw.cmd test
```

MockMvc로 실제 Thymeleaf 렌더링과 Controller → Service → Repository → SQL Server 흐름을 검증한다. 테스트 데이터는 각 테스트 종료 시 롤백된다. SQL Server의 IDENTITY 번호는 롤백해도 증가할 수 있다.

검증 결과 파일: `target/surefire-reports/*.txt`.

도전과제 제거 후 `clean package` 재검증: 총 11개 테스트 통과(기본 기동 1개, 학생 통합 테스트 10개). 학생 CRUD·검색·정렬·페이징 기능을 유지했다.

실행 가능한 JAR 경로: `target/backendintro-0.0.1-SNAPSHOT.jar`.

브라우저 연결 도구가 `sandboxPolicy` 누락 오류로 시작되지 않아 실제 브라우저에서의 시각적 레이아웃·클릭 검증은 미완료다. SSMS GUI를 직접 조작하지 않았으며, DB 검증은 실제 SQL Server를 사용하는 자동 테스트로 수행했다.

## 확인문제 학습 메모 (33쪽)

1. Controller는 HTTP 요청·응답과 모델을, Service는 업무 처리·트랜잭션을, Repository는 데이터 접근을 맡는다.
2. 모든 호출을 Service로 옮기기 전에 Repository를 제거하면 아직 남은 직접 호출이 컴파일되지 않는다.
3. `@Transactional`은 업무 처리 범위를 하나의 트랜잭션으로 묶어 성공 시 커밋하고 예외에 따라 롤백한다.
4. 트랜잭션 안에서 관리되는 영속 엔티티를 변경하면 flush 시 변경 감지가 수행된다.
5. `findBy`는 조회, `NameContaining`은 이름 부분 일치, `And`는 AND 결합, `MajorContaining`은 학과 부분 일치다.
6. 허용되지 않는 속성명으로 인한 쿼리 생성 오류를 막고 지원하는 정렬 기준만 적용한다.
7. 내부 번호가 0부터 시작하므로 `PageRequest.of(2, 5, sort)`는 화면의 3페이지다.
8. 페이지 링크에 검색·정렬 파라미터가 빠진 것이 원인이다. 모든 이동 링크에 조건을 함께 전달한다.
