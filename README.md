# JsonManager

JSON 파일 기반 CRUD 콘솔 애플리케이션 (Java 17 + Gson)

## 기능

| 기능 | 설명 |
|------|------|
| **Create** | 이름과 설명을 입력하여 새 데이터 추가 |
| **Read** | 저장된 전체 목록 조회 |
| **Search** | ID 일치 또는 이름/설명 키워드 부분 검색 |
| **Update** | ID로 항목을 선택하여 특정 필드 수정 |
| **Delete** | ID로 항목을 선택하여 확인 후 안전 삭제 |

## 프로젝트 구조

```
src/main/java/org/example/
├── Main.java                 # 진입점
├── model/Item.java           # 데이터 모델
├── repository/
│   └── JsonRepository.java  # JSON 파일 I/O (Gson)
├── service/
│   └── ItemService.java     # CRUD 비즈니스 로직
└── ui/
    └── ConsoleMenu.java     # 콘솔 메뉴 / 입력 처리
```

데이터는 `data/items.json`에 자동 생성 및 저장됩니다.

## 빌드 및 실행

```bash
# 빌드
./gradlew jar

# 실행
java -jar build/libs/JsonManager-1.0-SNAPSHOT.jar
```

## 데이터 형식

```json
[
  {
    "id": "1",
    "name": "예시 항목",
    "description": "설명 텍스트",
    "createdAt": "2026-06-11 12:00:00",
    "updatedAt": "2026-06-11 12:00:00"
  }
]
```

## 기술 스택

- Java 17
- Gradle 9.3
- [Gson 2.11.0](https://github.com/google/gson)
