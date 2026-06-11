package org.example.repository;

import org.example.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JsonRepository(tempDir.resolve("items.json").toString());
    }

    @Test
    void 초기_로드시_빈_리스트_반환() {
        List<Item> items = repository.loadAll();

        assertTrue(items.isEmpty());
    }

    @Test
    void 저장후_로드시_동일_데이터_반환() {
        Item item = new Item("1", "노트북", "MacBook Pro");
        repository.saveAll(List.of(item));

        List<Item> loaded = repository.loadAll();

        assertEquals(1, loaded.size());
        assertEquals("1", loaded.get(0).getId());
        assertEquals("노트북", loaded.get(0).getName());
        assertEquals("MacBook Pro", loaded.get(0).getDescription());
    }

    @Test
    void 여러건_저장_후_전체_반환() {
        List<Item> items = List.of(
                new Item("1", "노트북", "MacBook"),
                new Item("2", "마우스", "MX Master"),
                new Item("3", "키보드", "HHKB")
        );
        repository.saveAll(items);

        List<Item> loaded = repository.loadAll();

        assertEquals(3, loaded.size());
    }

    @Test
    void 빈_리스트_저장후_로드시_빈_리스트() {
        repository.saveAll(List.of(new Item("1", "임시", "데이터")));
        repository.saveAll(List.of());

        List<Item> loaded = repository.loadAll();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void 저장시_createdAt_updatedAt_유지() {
        Item item = new Item("1", "노트북", "MacBook");
        String createdAt = item.getCreatedAt();
        repository.saveAll(List.of(item));

        Item loaded = repository.loadAll().get(0);

        assertEquals(createdAt, loaded.getCreatedAt());
        assertEquals(createdAt, loaded.getUpdatedAt());
    }
}
