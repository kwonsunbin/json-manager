package org.example.service;

import org.example.model.Item;
import org.example.repository.JsonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    @TempDir
    Path tempDir;

    private ItemService service;

    @BeforeEach
    void setUp() {
        JsonRepository repository = new JsonRepository(tempDir.resolve("items.json").toString());
        service = new ItemService(repository);
    }

    // ── Create ──────────────────────────────────────────────

    @Test
    void create_항목_저장_후_반환() {
        Item item = service.create("노트북", "MacBook Pro");

        assertNotNull(item.getId());
        assertEquals("노트북", item.getName());
        assertEquals("MacBook Pro", item.getDescription());
        assertNotNull(item.getCreatedAt());
    }

    @Test
    void create_ID_자동_증가() {
        Item first = service.create("노트북", "MacBook");
        Item second = service.create("마우스", "MX Master");
        Item third = service.create("키보드", "HHKB");

        assertEquals("1", first.getId());
        assertEquals("2", second.getId());
        assertEquals("3", third.getId());
    }

    @Test
    void create_후_readAll_건수_증가() {
        service.create("노트북", "MacBook");
        service.create("마우스", "MX Master");

        assertEquals(2, service.readAll().size());
    }

    // ── Read ─────────────────────────────────────────────────

    @Test
    void readAll_초기_빈_리스트() {
        assertTrue(service.readAll().isEmpty());
    }

    @Test
    void readAll_저장된_전체_반환() {
        service.create("노트북", "MacBook");
        service.create("마우스", "MX Master");

        List<Item> all = service.readAll();

        assertEquals(2, all.size());
    }

    @Test
    void readById_존재하는_ID_반환() {
        Item created = service.create("노트북", "MacBook");

        Optional<Item> found = service.readById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void readById_없는_ID_빈_Optional() {
        Optional<Item> found = service.readById("999");

        assertTrue(found.isEmpty());
    }

    // ── Search ───────────────────────────────────────────────

    @Test
    void search_ID_완전일치() {
        service.create("노트북", "MacBook");
        service.create("마우스", "MX Master");

        List<Item> results = service.search("1");

        assertEquals(1, results.size());
        assertEquals("노트북", results.get(0).getName());
    }

    @Test
    void search_이름_키워드_부분일치() {
        service.create("노트북", "MacBook Pro");
        service.create("노트패드", "Samsung");
        service.create("마우스", "MX Master");

        List<Item> results = service.search("노트");

        assertEquals(2, results.size());
    }

    @Test
    void search_설명_키워드_대소문자_무시() {
        service.create("노트북", "MacBook Pro");
        service.create("마우스", "MX Master");

        List<Item> results = service.search("macbook");

        assertEquals(1, results.size());
        assertEquals("노트북", results.get(0).getName());
    }

    @Test
    void search_결과_없으면_빈_리스트() {
        service.create("노트북", "MacBook");

        List<Item> results = service.search("존재하지않는키워드");

        assertTrue(results.isEmpty());
    }

    // ── Update ───────────────────────────────────────────────

    @Test
    void update_name_수정() {
        Item item = service.create("노트북", "MacBook");

        boolean result = service.update(item.getId(), "name", "랩탑");

        assertTrue(result);
        assertEquals("랩탑", service.readById(item.getId()).get().getName());
    }

    @Test
    void update_description_수정() {
        Item item = service.create("노트북", "MacBook Pro 16");

        boolean result = service.update(item.getId(), "description", "MacBook Pro 14 M4");

        assertTrue(result);
        assertEquals("MacBook Pro 14 M4", service.readById(item.getId()).get().getDescription());
    }

    @Test
    void update_후_updatedAt_변경() throws InterruptedException {
        Item item = service.create("노트북", "MacBook");
        String originalUpdatedAt = item.getUpdatedAt();

        Thread.sleep(1000);
        service.update(item.getId(), "name", "랩탑");

        String newUpdatedAt = service.readById(item.getId()).get().getUpdatedAt();
        assertNotEquals(originalUpdatedAt, newUpdatedAt);
    }

    @Test
    void update_없는_ID는_false() {
        boolean result = service.update("999", "name", "테스트");

        assertFalse(result);
    }

    @Test
    void update_없는_필드는_false() {
        Item item = service.create("노트북", "MacBook");

        boolean result = service.update(item.getId(), "color", "silver");

        assertFalse(result);
    }

    // ── Delete ───────────────────────────────────────────────

    @Test
    void delete_항목_제거() {
        Item item = service.create("노트북", "MacBook");

        boolean result = service.delete(item.getId());

        assertTrue(result);
        assertTrue(service.readById(item.getId()).isEmpty());
    }

    @Test
    void delete_후_나머지_항목_유지() {
        service.create("노트북", "MacBook");
        Item second = service.create("마우스", "MX Master");

        service.delete("1");

        List<Item> remaining = service.readAll();
        assertEquals(1, remaining.size());
        assertEquals(second.getId(), remaining.get(0).getId());
    }

    @Test
    void delete_없는_ID는_false() {
        boolean result = service.delete("999");

        assertFalse(result);
    }
}
