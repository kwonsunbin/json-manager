package org.example.ui;

import org.example.model.Item;
import org.example.service.ItemService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {
    private final ItemService service;
    private final Scanner scanner;

    public ConsoleMenu(ItemService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   JSON 데이터 관리 시스템    ║");
        System.out.println("╚══════════════════════════════╝");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1" -> handleCreate();
                case "2" -> handleReadAll();
                case "3" -> handleSearch();
                case "4" -> handleUpdate();
                case "5" -> handleDelete();
                case "0" -> {
                    System.out.println("프로그램을 종료합니다.");
                    return;
                }
                default -> System.out.println("[오류] 올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n┌─────────────────────┐");
        System.out.println("│  1. Create (추가)   │");
        System.out.println("│  2. Read   (전체)   │");
        System.out.println("│  3. Search (검색)   │");
        System.out.println("│  4. Update (수정)   │");
        System.out.println("│  5. Delete (삭제)   │");
        System.out.println("│  0. 종료            │");
        System.out.println("└─────────────────────┘");
        System.out.print("선택 > ");
    }

    private void handleCreate() {
        System.out.println("[Create] 새 데이터 추가");
        System.out.print("  이름: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("[오류] 이름은 필수 입력입니다.");
            return;
        }
        System.out.print("  설명: ");
        String description = scanner.nextLine().trim();

        Item created = service.create(name, description);
        System.out.println("[완료] 데이터가 저장되었습니다.");
        System.out.println("  → " + created);
    }

    private void handleReadAll() {
        System.out.println("[Read] 전체 목록");
        List<Item> items = service.readAll();
        if (items.isEmpty()) {
            System.out.println("  저장된 데이터가 없습니다.");
            return;
        }
        System.out.println("  총 " + items.size() + "건");
        System.out.println("  " + "─".repeat(60));
        items.forEach(item -> System.out.println("  " + item));
    }

    private void handleSearch() {
        System.out.println("[Search] 데이터 검색");
        System.out.print("  검색어 (ID 또는 이름/설명 키워드): ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("[오류] 검색어를 입력해주세요.");
            return;
        }

        List<Item> results = service.search(keyword);
        if (results.isEmpty()) {
            System.out.println("  검색 결과가 없습니다.");
        } else {
            System.out.println("  검색 결과: " + results.size() + "건");
            System.out.println("  " + "─".repeat(60));
            results.forEach(item -> System.out.println("  " + item));
        }
    }

    private void handleUpdate() {
        System.out.println("[Update] 데이터 수정");
        System.out.print("  수정할 데이터 ID: ");
        String id = scanner.nextLine().trim();

        Optional<Item> found = service.readById(id);
        if (found.isEmpty()) {
            System.out.println("[오류] ID [" + id + "]에 해당하는 데이터가 없습니다.");
            return;
        }

        System.out.println("  현재 데이터: " + found.get());
        System.out.println("  수정할 필드를 선택하세요:");
        System.out.println("    1. 이름 (name)");
        System.out.println("    2. 설명 (description)");
        System.out.print("  선택 > ");
        String fieldChoice = scanner.nextLine().trim();

        String field = switch (fieldChoice) {
            case "1" -> "name";
            case "2" -> "description";
            default -> null;
        };

        if (field == null) {
            System.out.println("[오류] 올바른 필드를 선택해주세요.");
            return;
        }

        System.out.print("  새로운 값: ");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("[오류] 값을 입력해주세요.");
            return;
        }

        if (service.update(id, field, value)) {
            System.out.println("[완료] 데이터가 수정되었습니다.");
            service.readById(id).ifPresent(item -> System.out.println("  → " + item));
        } else {
            System.out.println("[오류] 수정에 실패했습니다.");
        }
    }

    private void handleDelete() {
        System.out.println("[Delete] 데이터 삭제");
        System.out.print("  삭제할 데이터 ID: ");
        String id = scanner.nextLine().trim();

        Optional<Item> found = service.readById(id);
        if (found.isEmpty()) {
            System.out.println("[오류] ID [" + id + "]에 해당하는 데이터가 없습니다.");
            return;
        }

        System.out.println("  삭제할 데이터: " + found.get());
        System.out.print("  정말 삭제하시겠습니까? (y/n): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("  삭제가 취소되었습니다.");
            return;
        }

        if (service.delete(id)) {
            System.out.println("[완료] 데이터가 삭제되었습니다.");
        } else {
            System.out.println("[오류] 삭제에 실패했습니다.");
        }
    }
}
