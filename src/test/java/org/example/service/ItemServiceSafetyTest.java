package org.example.service;

import org.example.model.Item;
import org.example.repository.JsonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceSafetyTest {

    @TempDir
    Path tempDir;

    private ItemService service;

    @BeforeEach
    void setUp() {
        JsonRepository repository = new JsonRepository(tempDir.resolve("items.json").toString());
        service = new ItemService(repository);
    }

    // ── Null / 빈 문자열 입력 ─────────────────────────────────

    @Test
    void create_null_name_저장됨() {
        Item item = service.create(null, "설명");
        assertNotNull(item);
        assertNull(item.getName());
    }

    @Test
    void create_null_description_저장됨() {
        Item item = service.create("이름", null);
        assertNotNull(item);
        assertNull(item.getDescription());
    }

    @Test
    void create_빈_문자열_name_저장됨() {
        Item item = service.create("", "설명");
        assertNotNull(item);
        assertEquals("", item.getName());
    }

    @Test
    void search_null_키워드_예외_발생() {
        service.create("노트북", "MacBook");
        assertThrows(NullPointerException.class, () -> service.search(null));
    }

    @Test
    void search_빈_문자열_전체_반환() {
        service.create("노트북", "MacBook");
        service.create("마우스", "MX Master");
        List<Item> results = service.search("");
        assertEquals(2, results.size());
    }

    @Test
    void update_null_값_설정_성공() {
        Item item = service.create("노트북", "MacBook");
        boolean result = service.update(item.getId(), "name", null);
        assertTrue(result);
        assertNull(service.readById(item.getId()).get().getName());
    }

    @Test
    void update_빈_문자열_설정_성공() {
        Item item = service.create("노트북", "MacBook");
        boolean result = service.update(item.getId(), "name", "");
        assertTrue(result);
        assertEquals("", service.readById(item.getId()).get().getName());
    }

    @Test
    void update_null_ID_false_반환() {
        assertFalse(service.update(null, "name", "값"));
    }

    @Test
    void delete_null_ID_false_반환() {
        assertFalse(service.delete(null));
    }

    @Test
    void delete_빈_문자열_ID_false_반환() {
        assertFalse(service.delete(""));
    }

    // ── 특수문자 / Injection 안전성 ──────────────────────────

    @Test
    void create_JSON_특수문자_저장_후_복원() {
        String name = "item\"with\\slash";
        Item created = service.create(name, "설명");
        Item loaded = service.readById(created.getId()).get();
        assertEquals(name, loaded.getName());
    }

    @Test
    void create_HTML_특수문자_저장_후_복원() {
        String name = "<script>alert('xss')</script>";
        Item created = service.create(name, "설명");
        Item loaded = service.readById(created.getId()).get();
        assertEquals(name, loaded.getName());
    }

    @Test
    void create_유니코드_이모지_저장_후_복원() {
        String name = "아이템 🎉";
        Item created = service.create(name, "설명 ✨");
        Item loaded = service.readById(created.getId()).get();
        assertEquals(name, loaded.getName());
    }

    @Test
    void create_줄바꿈_포함_저장_후_복원() {
        String description = "line1\nline2\r\nline3";
        Item created = service.create("이름", description);
        Item loaded = service.readById(created.getId()).get();
        assertEquals(description, loaded.getDescription());
    }

    // ── 경계값 ───────────────────────────────────────────────

    @Test
    void create_매우_긴_문자열_저장_후_복원() {
        String longName = "가".repeat(10_000);
        Item created = service.create(longName, "설명");
        Item loaded = service.readById(created.getId()).get();
        assertEquals(longName, loaded.getName());
    }

    @Test
    void 대량_항목_생성_후_전체_조회() {
        for (int i = 0; i < 1_000; i++) {
            service.create("항목" + i, "설명" + i);
        }
        assertEquals(1_000, service.readAll().size());
    }

    @Test
    void 대량_항목_생성_후_검색_정확도() {
        for (int i = 0; i < 500; i++) {
            service.create("일반항목" + i, "desc");
        }
        for (int i = 0; i < 10; i++) {
            service.create("특별항목" + i, "special");
        }
        List<Item> results = service.search("특별");
        assertEquals(10, results.size());
    }

    // ── 동시성 ───────────────────────────────────────────────

    @Test
    void 동시_생성_ID_중복_없음() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    service.create("항목" + idx, "설명" + idx);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        List<Item> all = service.readAll();
        Set<String> ids = all.stream().map(Item::getId).collect(Collectors.toSet());

        assertEquals(all.size(), ids.size(), "ID 중복이 발생했습니다 (스레드 안전성 부재)");
        assertEquals(threadCount, all.size(), "일부 항목이 유실되었습니다 (스레드 안전성 부재)");
    }
}
