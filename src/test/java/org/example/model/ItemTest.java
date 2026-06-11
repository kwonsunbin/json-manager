package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void 생성자_필드_정상_설정() {
        Item item = new Item("1", "노트북", "MacBook Pro");

        assertEquals("1", item.getId());
        assertEquals("노트북", item.getName());
        assertEquals("MacBook Pro", item.getDescription());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    void 생성시_createdAt과_updatedAt이_동일() {
        Item item = new Item("1", "노트북", "MacBook Pro");

        assertEquals(item.getCreatedAt(), item.getUpdatedAt());
    }

    @Test
    void setter_값_변경() {
        Item item = new Item("1", "노트북", "MacBook Pro");

        item.setName("마우스");
        item.setDescription("MX Master 3");
        item.setUpdatedAt("2026-06-11 12:00:00");

        assertEquals("마우스", item.getName());
        assertEquals("MX Master 3", item.getDescription());
        assertEquals("2026-06-11 12:00:00", item.getUpdatedAt());
    }

    @Test
    void toString_ID와_이름_포함() {
        Item item = new Item("42", "키보드", "HHKB");

        String result = item.toString();

        assertTrue(result.contains("42"));
        assertTrue(result.contains("키보드"));
        assertTrue(result.contains("HHKB"));
    }

    @Test
    void 기본_생성자_필드_null() {
        Item item = new Item();

        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
    }
}
