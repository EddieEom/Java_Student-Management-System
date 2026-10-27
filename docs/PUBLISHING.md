# GitHub / GitHub Pages 공개 절차

이 프로젝트는 로컬 Git 이력에 기초 실습부터 포트폴리오까지 **상세 커밋 기록**을 보존하고 있습니다. 게시 대상 저장소는 `EddieEom/Java_Student-Management-System`이며, 실제 원격에 반영된 커밋 수는 GitHub의 Commits 탭에서 확인해야 합니다.

## 1. 공개 전 확인

- `.env`, 실제 DB 계정/비밀번호, 강의 원본 PDF 등 민감하거나 재배포 권한이 없는 자료가 없는지 확인합니다.
- 본인과 타인의 개인정보가 포함된 학생 화면은 일부 식별 정보를 흐리게 처리한 이미지로 사용합니다.
- README의 AI 활용·기여 범위·검증 근거는 사실 그대로 유지합니다.
- 로컬 SQL Server 테스트 증빙이 필요하면 인증정보를 가리고 SQL UPDATE 로그, Postman 상태코드 캡처를 추가로 업로드합니다.

## 2. 기존 GitHub 리포지토리

배포 대상: [EddieEom/Java_Student-Management-System](https://github.com/EddieEom/Java_Student-Management-System)

기존 리포지토리를 사용하므로 새 리포지토리를 만들 필요는 없습니다. GitHub 연결 기능으로 코드를 올리는 경우 원본 로컬 12개 커밋과 원격의 커밋 그래프는 동일하지 않을 수 있습니다. 상세한 로컬 변경 기록은 ZIP 내부 `.git`과 `Campus-Portfolio-Git-History.txt`에 남아 있습니다.

개인 PC에서 직접 Push할 때는 다음 명령을 참고합니다. 단, 이미 원격에 커밋이 존재하면 먼저 `git fetch` 후 히스토리를 비교하고 **force push를 하지 마세요**.

```bash
git remote add origin https://github.com/EddieEom/Java_Student-Management-System.git
git fetch origin
git log --oneline --all --graph -15
```

## 3. GitHub Pages 활성화

1. 새 GitHub 리포지토리의 `Settings → Pages`를 엽니다.
2. `Build and deployment → Source: Deploy from a branch`를 선택합니다.
3. `Branch: main`, `Folder: /docs`를 선택하고 Save합니다.
4. GitHub Pages에 표시된 실제 URL을 복사해 README 상단과 GitHub 저장소 설명의 Website 입력란에 추가합니다.

`docs/index.html`이 포트폴리오 메인 페이지입니다. **학생·과정 CRUD 서버는 별개로 SQL Server와 Spring Boot가 있어야 동작**하며 GitHub Pages는 HTML·CSS·이미지로 된 소개 페이지만 호스팅합니다.

## 4. 증빙 스크린샷 업데이트할 때

- 새 캡처는 `docs/screenshots/`에 저장합니다.
- README와 `docs/index.html`의 이미지 경로를 일치시킵니다.
- 학생 개인정보와 DB 비밀번호가 노출되지 않았는지 확인합니다.
- 커밋 메시지에 **왜 수정했는지, 어떤 기능이 확인됐는지**를 기록합니다.

## 5. 제안하는 다음 커밋 형식

```text
docs(evidence): attach redacted Postman and SSMS test screenshots

- Capture Course POST 201, PUT 200, invalid POST 400, DELETE 204 and deleted GET 404.
- Show SQL UPDATE after changing an existing Course without explicit save().
- Redact SQL Server users, passwords and personal student data.
- Explain test environment and exact observed results in README.
```
