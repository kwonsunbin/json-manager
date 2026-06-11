# CLAUDE.md

## Project

JsonManager

JSON 파일 기반 CRUD 콘솔 애플리케이션

### Tech Stack

* Java 17
* Gradle 9.3
* Gson 2.11.0

---

# Project Goal

사용자가 콘솔 환경에서 데이터를 생성(Create), 조회(Read), 검색(Search), 수정(Update), 삭제(Delete)할 수 있는 JSON 기반 CRUD 애플리케이션을 개발한다.

데이터는 `data/items.json` 파일에 저장한다.

---

# Project Structure

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

---

# Required Development Flow

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
Implementation
```

## 1. PRD.md

먼저 Product Requirement를 작성한다.

포함 내용:

* 기능 요구사항
* 비기능 요구사항
* 사용자 시나리오
* 예외 처리 정책

---

## 2. SPEC.md

PRD를 기반으로 기술 설계를 작성한다.

포함 내용:

* 클래스 설계
* 데이터 구조
* 메서드 시그니처
* 파일 구조
* 예외 처리 방식

---

## 3. PLAN.md

구현 계획을 작성한다.

포함 내용:

* 작업 순서
* 예상 변경 파일
* 테스트 전략
* 리스크 분석

---

## 4. Implementation

PLAN 승인 후 구현한다.

구현 전에 코드를 작성하지 않는다.

---

# TDD Rules

모든 기능 구현은 TDD를 따른다.

반드시 아래 사이클을 유지한다.

```text
RED
↓
GREEN
↓
REFACTOR
```

## RED

먼저 실패하는 테스트 작성

* 요구사항 검증
* 실패 확인

## GREEN

최소한의 코드로 테스트 통과

* 과도한 구현 금지
* 테스트 통과 우선

## REFACTOR

구조 개선

* 중복 제거
* 가독성 향상
* 책임 분리

테스트는 항상 성공 상태를 유지해야 한다.

---

# Regression Test Rules

기존 기능을 수정하는 경우 반드시 회귀 테스트를 작성한다.

변경 전 확인:

* Create
* Read
* Search
* Update
* Delete

기존 기능이 깨지지 않았음을 증명해야 한다.

---

# Safety Rules

기능 수정 시 아래를 반드시 검증한다.

## Data Safety

절대로 기존 데이터를 손실시키지 않는다.

삭제 시:

* 사용자 확인 필요
* 즉시 삭제 금지

업데이트 시:

* 대상 ID 존재 여부 확인

저장 시:

* 기존 데이터 유지
* 전체 파일 무결성 유지

---

## File Safety

절대로 아래 파일을 임의 삭제하지 않는다.

```text
data/items.json
build.gradle
settings.gradle
```

---

## JSON Safety

JSON 저장 전 반드시 검증한다.

검증 항목:

* JSON 파싱 가능
* ID 존재
* name null 아님
* createdAt 유지
* updatedAt 갱신

---

# Architecture Rules

## Model

Item은 데이터만 가진다.

비즈니스 로직 금지.

---

## Repository

JsonRepository 책임:

* 파일 생성
* 파일 읽기
* 파일 쓰기

비즈니스 로직 금지.

---

## Service

ItemService 책임:

* CRUD 처리
* 검색 처리
* 검증 처리

파일 I/O 금지.

---

## UI

ConsoleMenu 책임:

* 입력 처리
* 메뉴 출력

비즈니스 로직 금지.

---

# Coding Rules

## General

* 단일 책임 원칙 준수
* 의미 있는 이름 사용
* 매직 넘버 금지
* 중복 코드 금지

---

## Methods

한 메서드는 하나의 책임만 가진다.

권장:

```java
<= 30 lines
```

---

## Exception Handling

예외를 무시하지 않는다.

금지:

```java
catch(Exception e){}
```

허용:

```java
catch(IOException e){
    throw new RuntimeException(e);
}
```

---

# Testing Rules

신규 기능 추가 시 반드시 테스트 추가.

테스트 우선순위:

1. Service
2. Repository
3. UI

---

# Commit Rules

커밋 메시지 형식:

```text
feat: add item search
fix: prevent duplicate id
refactor: simplify repository logic
test: add update service tests
```

---

# Pull Request Rules

작업 완료 후:

1. 테스트 실행
2. 변경사항 요약 작성
3. 커밋 생성
4. 원격 브랜치 푸시
5. PR 생성 (.github/PULL_REQUEST_TEMPLATE.md)

---

# Instructions For Claude

작업 시 반드시 아래 형식으로 진행한다.

## Step 1

요구사항 분석

## Step 2

PRD 작성

## Step 3

SPEC 작성

## Step 4

PLAN 작성

## Step 5

RED 테스트 작성

## Step 6

GREEN 구현

## Step 7

REFACTOR

## Step 8

Regression Test 수행

## Step 9

변경사항 요약

코드만 바로 작성하지 않는다.

항상 설계 → 계획 → 테스트 → 구현 순서를 따른다.