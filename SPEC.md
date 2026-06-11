# SPEC.md — Technical Specification

## 아키텍처 개요

```
ConsoleMenu (UI)
    ↓ 호출
ItemService (Business Logic)
    ↓ 호출
JsonRepository (File I/O)
    ↓ 읽기/쓰기
data/items.json
```

레이어 간 의존 방향은 단방향이며, 각 레이어는 자신의 책임만 가진다.

---

## 클래스 설계

### 1. Item — `org.example.model.Item`

데이터 컨테이너. 비즈니스 로직 없음.

**필드**

| 필드 | 타입 | 설명 |
|------|------|------|
| id | String | 고유 식별자 (자동 부여, 정수 문자열) |
| name | String | 항목 이름 (필수) |
| description | String | 항목 설명 (선택) |
| createdAt | String | 생성 시각 (`yyyy-MM-dd HH:mm:ss`) |
| updatedAt | String | 최종 수정 시각 (`yyyy-MM-dd HH:mm:ss`) |

**생성자**

```java
public Item()                                          // Gson 역직렬화용 기본 생성자
public Item(String id, String name, String description) // id, name, description으로 생성; createdAt/updatedAt 자동 설정
```

**메서드**

```java
// getter/setter (id, name, description, createdAt, updatedAt)
public String toString() // "[ID: {id}] {name} | {description} (생성: {createdAt} / 수정: {updatedAt})"
```

---

### 2. JsonRepository — `org.example.repository.JsonRepository`

파일 I/O 전담. 비즈니스 로직 없음.

**필드**

| 필드 | 타입 | 설명 |
|------|------|------|
| filePath | String | JSON 파일 경로 |
| gson | Gson | Pretty-print 설정된 Gson 인스턴스 |

**생성자**

```java
public JsonRepository()                  // "data/items.json" 경로 사용
public JsonRepository(String filePath)   // 지정 경로 사용 (테스트용)
```

**메서드**

```java
public List<Item> loadAll()              // JSON 파일 전체 읽기; 파일 없거나 비어있으면 빈 리스트 반환
public void saveAll(List<Item> items)    // 전체 리스트를 JSON 파일에 덮어쓰기
private void ensureFileExists()          // 디렉토리 및 파일 없으면 생성 ("[]" 초기화)
```

**예외 처리**

```java
// IOException → RuntimeException으로 래핑하여 상위 전달
```

---

### 3. ItemService — `org.example.service.ItemService`

CRUD 비즈니스 로직 전담. 파일 I/O 없음.

**필드**

| 필드 | 타입 | 설명 |
|------|------|------|
| repository | JsonRepository | 의존성 주입 |
| FORMATTER | DateTimeFormatter | `yyyy-MM-dd HH:mm:ss` 포맷 상수 |

**생성자**

```java
public ItemService(JsonRepository repository)
```

**메서드**

```java
public synchronized Item create(String name, String description)
// 새 Item 생성, ID 자동 부여 (기존 최대 ID + 1), 저장 후 반환

public List<Item> readAll()
// 전체 항목 반환

public Optional<Item> readById(String id)
// ID로 단일 항목 조회; 없으면 Optional.empty()

public List<Item> search(String keyword)
// ID 완전 일치 OR name/description 대소문자 무시 부분 일치

public synchronized boolean update(String id, String field, String value)
// 필드(name 또는 description) 수정, updatedAt 갱신; 성공 true / 없는 ID 또는 잘못된 필드 false

public synchronized boolean delete(String id)
// ID 항목 삭제; 성공 true / 없는 ID false

private int generateNextId(List<Item> items)
// 기존 최대 ID + 1 반환; 항목 없으면 1 반환
```

---

### 4. ConsoleMenu — `org.example.ui.ConsoleMenu`

입출력 전담. 비즈니스 로직 없음.

**필드**

| 필드 | 타입 | 설명 |
|------|------|------|
| service | ItemService | 의존성 주입 |
| scanner | Scanner | 표준 입력 |

**생성자**

```java
public ConsoleMenu(ItemService service)
```

**메서드**

```java
public void run()                 // 메인 루프 (0 입력 시 종료)
private void printMenu()          // 메뉴 출력
private void handleCreate()       // Create 처리
private void handleReadAll()      // Read 처리
private void handleSearch()       // Search 처리
private void handleUpdate()       // Update 처리
private void handleDelete()       // Delete 처리
```

---

### 5. Main — `org.example.Main`

진입점. 의존성 조립 후 ConsoleMenu 실행.

```java
public static void main(String[] args) {
    JsonRepository repository = new JsonRepository();
    ItemService service = new ItemService(repository);
    ConsoleMenu menu = new ConsoleMenu(service);
    menu.run();
}
```

---

## 데이터 구조

### JSON 파일 형식 (`data/items.json`)

```json
[
  {
    "id": "1",
    "name": "노트북",
    "description": "MacBook Pro 14인치 M4",
    "createdAt": "2026-06-11 12:00:00",
    "updatedAt": "2026-06-11 12:05:00"
  }
]
```

- 최상위 배열 구조
- 빈 상태: `[]`
- 인코딩: UTF-8
- 형식: Pretty-print (GsonBuilder.setPrettyPrinting())

### ID 생성 규칙

- 정수 문자열 ("1", "2", "3", ...)
- 기존 최대 ID + 1
- 삭제 후 재사용 없음 (단순 증가)
- 파싱 불가능한 ID는 0으로 취급

### 날짜 형식

```
yyyy-MM-dd HH:mm:ss
예: 2026-06-11 12:00:00
```

---

## 파일 구조

```
JsonManager/
├── src/
│   ├── main/java/org/example/
│   │   ├── Main.java
│   │   ├── model/Item.java
│   │   ├── repository/JsonRepository.java
│   │   ├── service/ItemService.java
│   │   └── ui/ConsoleMenu.java
│   └── test/java/org/example/
│       ├── model/ItemTest.java
│       ├── repository/JsonRepositoryTest.java
│       ├── repository/JsonRepositorySafetyTest.java
│       ├── service/ItemServiceTest.java
│       └── service/ItemServiceSafetyTest.java
├── data/
│   └── items.json
├── build.gradle
├── settings.gradle
├── CLAUDE.md
├── PRD.md        (gitignore)
├── SPEC.md       (gitignore)
└── PLAN.md       (gitignore)
```

---

## 예외 처리 방식

| 레이어 | 예외 | 처리 방식 |
|--------|------|-----------|
| JsonRepository | IOException | RuntimeException으로 래핑 후 상위 전달 |
| ItemService | (없음) | Repository 예외 그대로 전파 |
| ConsoleMenu | (없음) | Service 예외 그대로 전파 |

금지 패턴:

```java
catch (Exception e) {}  // 예외 무시 금지
```

허용 패턴:

```java
catch (IOException e) {
    throw new RuntimeException("데이터 로드 실패: " + e.getMessage(), e);
}
```

---

## 동시성 설계

Create, Update, Delete는 `synchronized` 키워드로 보호된다.

```java
public synchronized Item create(String name, String description) { ... }
public synchronized boolean update(String id, String field, String value) { ... }
public synchronized boolean delete(String id) { ... }
```

Read / Search는 동기화 불필요 (파일 상태 변경 없음).

---

## 테스트 설계

### 테스트 파일 구조

| 파일 | 테스트 대상 |
|------|------------|
| ItemTest | Item 생성자, getter/setter, toString |
| JsonRepositoryTest | loadAll, saveAll, 파일 생성, 데이터 무결성 |
| JsonRepositorySafetyTest | createdAt/updatedAt 유지, 기존 데이터 보존 |
| ItemServiceTest | Create/Read/Search/Update/Delete 전체 케이스 |
| ItemServiceSafetyTest | 동시성, 데이터 손실 방지, ID 무결성 |

### 테스트 원칙

- `@TempDir`을 사용하여 실제 `data/items.json`과 격리
- 각 테스트는 독립적으로 실행 가능
- 한국어 메서드명으로 의도 명확화
