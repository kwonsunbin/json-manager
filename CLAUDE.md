# CLAUDE.md

# Project

## JsonManager

JSON 파일 기반 CRUD 콘솔 애플리케이션

### Tech Stack

* Java 17
* Gradle 9.3
* Gson 2.11.0

### Goal

사용자가 콘솔 환경에서 데이터를 생성(Create), 조회(Read), 검색(Search), 수정(Update), 삭제(Delete)할 수 있는 JSON 기반 CRUD 애플리케이션을 개발한다.

데이터는 `data/items.json` 에 저장한다.

---

# Architecture

## Project Structure

```text
src/main/java/org/example/
├── Main.java
├── model/
│   └── Item.java
├── repository/
│   └── JsonRepository.java
├── service/
│   └── ItemService.java
└── ui/
    └── ConsoleMenu.java
```

## Layer Responsibilities

### Model

* 데이터만 보관
* 비즈니스 로직 금지

### Repository

* JSON 파일 생성
* JSON 파일 읽기
* JSON 파일 쓰기

비즈니스 로직 금지

### Service

* CRUD 처리
* 검색 처리
* 검증 처리

파일 I/O 금지

### UI

* 메뉴 출력
* 사용자 입력 처리

비즈니스 로직 금지

---

# Development Workflow

모든 작업은 반드시 아래 순서를 따른다.

```text
CLAUDE.md
    ↓
PRD.md
    ↓
SPEC.md
    ↓
PLAN.md
    ↓
RED
    ↓
GREEN
    ↓
REFACTOR
    ↓
Regression Test
    ↓
PR
```

## PRD.md

포함 내용

* 기능 요구사항
* 비기능 요구사항
* 사용자 시나리오
* 예외 처리 정책

## SPEC.md

포함 내용

* 클래스 설계
* 데이터 구조
* 메서드 시그니처
* 파일 구조
* 예외 처리 방식

## PLAN.md

포함 내용

* 작업 순서
* 예상 변경 파일
* 테스트 전략
* 리스크 분석

구현 전에 반드시 PLAN 작성 완료.

---

# TDD & Testing

모든 기능 구현은 TDD를 따른다.

## RED

* 실패하는 테스트 작성
* 실패 확인

## GREEN

* 최소 구현으로 테스트 통과

## REFACTOR

* 중복 제거
* 가독성 향상
* 구조 개선

## Test Priority

1. Service
2. Repository
3. UI

## Regression Test

기존 기능 수정 시 반드시 검증

* Create
* Read
* Search
* Update
* Delete

---

# Safety Rules

## Data Safety

* 기존 데이터 손실 금지
* 삭제 시 사용자 확인 필요
* 업데이트 시 ID 존재 여부 검증

## JSON Safety

저장 전 검증

* JSON 파싱 가능
* ID 존재
* name != null
* createdAt 유지
* updatedAt 갱신

## Protected Files

절대 삭제 금지

```text
data/items.json
build.gradle
settings.gradle
```

---

# Git & Worktree Workflow

이 프로젝트는 Worktree First 전략을 사용한다.

## Rules

* main 브랜치 직접 개발 금지
* main 브랜치 직접 commit 금지
* main 브랜치 직접 push 금지
* 모든 변경은 PR로 반영
* 하나의 Worktree = 하나의 기능
* 하나의 Worktree = 하나의 PR

## Start

```bash
git checkout main
git pull origin main

git branch feature/<feature-name>

git worktree add \
  ../JsonManager-<feature-name> \
  feature/<feature-name>
```

## Finish

```bash
git push origin feature/<feature-name>

gh pr create
```

Merge 후

```bash
git worktree remove <worktree-path>

git branch -d feature/<feature-name>
```

## Claude Rule

작업 시작 시 확인

```bash
git branch --show-current
git worktree list
```

현재 브랜치가 main이면 구현 시작 금지.

---

# Coding Rules

* 단일 책임 원칙 준수
* 의미 있는 이름 사용
* 매직 넘버 금지
* 중복 코드 금지

## Method Rule

권장:

```text
1 Method = 1 Responsibility
<= 30 Lines
```

## Exception Rule

금지

```java
catch(Exception e){}
```

허용

```java
catch(IOException e){
    throw new RuntimeException(e);
}
```

---

# Commit Rules

Commit 메시지는 Conventional Commits 규칙을 따른다.

형식:

```text
<type>: <summary>
```

예시:

```text
feat: add item search feature
fix: prevent duplicate item id
refactor: simplify repository logic
test: add item service tests
docs: update README
```

## Allowed Types

### feat

새로운 기능 추가

```text
feat: add keyword search
```

### fix

버그 수정

```text
fix: handle missing json file
```

### refactor

동작 변경 없이 코드 구조 개선

```text
refactor: extract validation logic
```

### test

테스트 추가 또는 수정

```text
test: add update service tests
```

### docs

문서 수정

```text
docs: update CLAUDE workflow
```

### chore

빌드, 설정, 의존성 변경

```text
chore: upgrade gson to 2.11.0
```

### style

코드 포맷팅, 공백, import 정리

```text
style: format repository classes
```

### perf

성능 개선

```text
perf: optimize item search
```

## Rules

* 커밋은 하나의 논리적 변경만 포함한다.
* 여러 기능을 하나의 커밋으로 묶지 않는다.
* 의미 없는 메시지 금지.

금지 예시:

```text
update
fix
test
wip
asdf
```

권장:

```text
feat: add item update command
fix: validate empty item name
refactor: move json parsing to repository
```


---

# Pull Request Rules

PR 생성 전 확인

* [ ] PRD 최신화
* [ ] SPEC 최신화
* [ ] PLAN 최신화
* [ ] 모든 테스트 통과
* [ ] Regression Test 완료

PR 템플릿 사용

```text
.github/PULL_REQUEST_TEMPLATE.md
```

---

# Definition of Done

작업 완료 조건

* [ ] PRD 작성
* [ ] SPEC 작성
* [ ] PLAN 작성
* [ ] RED 테스트 작성
* [ ] GREEN 구현
* [ ] REFACTOR 완료
* [ ] Regression Test 통과
* [ ] Commit 완료
* [ ] Push 완료
* [ ] Pull Request 생성

```
```

## Subagents

### implement-agent

기능 구현 담당

- GREEN 구현
- REFACTOR
- Architecture 준수

### test-agent

테스트 담당

- RED 테스트 작성
- Regression Test 작성
- Edge Case 검증

규칙:

1. test-agent → RED 작성
2. implement-agent → GREEN 구현
3. implement-agent → REFACTOR
4. test-agent → Regression Test 검증
