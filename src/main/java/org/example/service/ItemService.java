package org.example.service;

import org.example.model.Item;
import org.example.repository.JsonRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemService {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JsonRepository repository;

    public ItemService(JsonRepository repository) {
        this.repository = repository;
    }

    public synchronized Item create(String name, String description) {
        List<Item> items = repository.loadAll();
        String id = String.valueOf(generateNextId(items));
        Item item = new Item(id, name, description);
        items.add(item);
        repository.saveAll(items);
        return item;
    }

    public List<Item> readAll() {
        return repository.loadAll();
    }

    public Optional<Item> readById(String id) {
        return repository.loadAll().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public List<Item> search(String keyword) {
        String lower = keyword.toLowerCase();
        return repository.loadAll().stream()
                .filter(item ->
                        item.getId().equals(keyword) ||
                        item.getName().toLowerCase().contains(lower) ||
                        item.getDescription().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public synchronized boolean update(String id, String field, String value) {
        List<Item> items = repository.loadAll();
        for (Item item : items) {
            if (item.getId().equals(id)) {
                switch (field.toLowerCase()) {
                    case "name" -> item.setName(value);
                    case "description" -> item.setDescription(value);
                    default -> { return false; }
                }
                item.setUpdatedAt(LocalDateTime.now().format(FORMATTER));
                repository.saveAll(items);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean delete(String id) {
        List<Item> items = repository.loadAll();
        boolean removed = items.removeIf(item -> item.getId().equals(id));
        if (removed) {
            repository.saveAll(items);
        }
        return removed;
    }

    private int generateNextId(List<Item> items) {
        return items.stream()
                .mapToInt(i -> {
                    try { return Integer.parseInt(i.getId()); }
                    catch (NumberFormatException e) { return 0; }
                })
                .max()
                .orElse(0) + 1;
    }
}
