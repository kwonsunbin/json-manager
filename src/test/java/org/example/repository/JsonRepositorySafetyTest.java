package org.example.repository;

import org.example.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonRepositorySafetyTest {

    @TempDir
    Path tempDir;

    private Path jsonFile;

    @BeforeEach
    void setUp() {
        jsonFile = tempDir.resolve("items.json");
    }

    @Test
    void 손상된_JSON_파일_로드시_예외_발생() throws IOException {
        Files.writeString(jsonFile, "{invalid json content", StandardCharsets.UTF_8);
        JsonRepository repository = new JsonRepository(jsonFile.toString());
        assertThrows(RuntimeException.class, repository::loadAll);
    }

    @Test
    void 완전히_빈_파일_로드시_빈_리스트_반환() throws IOException {
        // Gson은 빈 파일을 null로 역직렬화 → 빈 리스트로 대체 (예외 없음)
        Files.writeString(jsonFile, "", StandardCharsets.UTF_8);
        JsonRepository repository = new JsonRepository(jsonFile.toString());
        List<Item> loaded = repository.loadAll();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void null_리스트_저장시_null_기록_이후_로드_빈_리스트() {
        JsonRepository repository = new JsonRepository(jsonFile.toString());
        // Gson은 null을 "null" 문자열로 직렬화하므로 예외 없이 저장됨
        assertDoesNotThrow(() -> repository.saveAll(null));
        // "null"을 역직렬화하면 null → 빈 리스트로 대체
        List<Item> loaded = repository.loadAll();
        assertTrue(loaded.isEmpty());
    }
}
