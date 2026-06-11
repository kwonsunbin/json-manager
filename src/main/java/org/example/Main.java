package org.example;

import org.example.repository.JsonRepository;
import org.example.service.ItemService;
import org.example.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        JsonRepository repository = new JsonRepository();
        ItemService service = new ItemService(repository);
        ConsoleMenu menu = new ConsoleMenu(service);
        menu.run();
    }
}
