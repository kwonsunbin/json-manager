package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.model.Item;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository {
    private static final String FILE_PATH = "data/items.json";
    private final Gson gson;

    public JsonRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            Path path = Paths.get(FILE_PATH);
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.writeString(path, "[]");
            }
        } catch (IOException e) {
            throw new RuntimeException("JSON 파일 초기화 실패: " + e.getMessage(), e);
        }
    }

    public List<Item> loadAll() {
        try (Reader reader = new InputStreamReader(
                new FileInputStream(FILE_PATH), StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<Item>>() {}.getType();
            List<Item> items = gson.fromJson(reader, listType);
            return items != null ? items : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("데이터 로드 실패: " + e.getMessage(), e);
        }
    }

    public void saveAll(List<Item> items) {
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(FILE_PATH), StandardCharsets.UTF_8)) {
            gson.toJson(items, writer);
        } catch (IOException e) {
            throw new RuntimeException("데이터 저장 실패: " + e.getMessage(), e);
        }
    }
}
