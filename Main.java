import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Simple Inventory Tracker (PS No. 25)
 * Console-based inventory manager for a small shop.
 * Concepts: ArrayList, Searching, Sorting, Methods, File Handling.
 *
 * Menu: 1 Add, 2 Display, 3 Search, 4 Sell, 5 Low stock, 6 Sort by quantity, 7 Exit.
 * Data is stored in inventory.txt (loaded at start, saved on exit).
 */
public class Main {

    static final String FILE_NAME = "inventory.txt";
    static final int LOW_STOCK_LIMIT = 5;   // low stock = quantity below 5
    static final int FIRST_ID_NUMBER = 101; // first generated ID is I101

    /** One inventory record. */
    static class Item {
        String id;
        String name;
        int quantity;
        double price;

        Item(String id, String name, int quantity, double price) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
    }

    static ArrayList<Item> items = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    // ------------------------------------------------------------------
    // Main
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        loadFromFile();

        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: addItem(); break;
                case 2: displayItems(); break;
                case 3: searchItem(); break;
                case 4: sellItem(); break;
                case 5: lowStockReport(); break;
                case 6: sortByQuantity(); break;
                case 7:
                    saveToFile();
                    System.out.println("Data saved to " + FILE_NAME + ". Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 7.");
            }
        }
        sc.close();
    }

    static void showMenu() {
        System.out.println();
        System.out.println("===== SIMPLE INVENTORY TRACKER =====");
        System.out.println("1. Add item");
        System.out.println("2. Display stock");
        System.out.println("3. Search item");
        System.out.println("4. Sell item");
        System.out.println("5. Low stock report");
        System.out.println("6. Sort by quantity");
        System.out.println("7. Exit");
    }

    // ------------------------------------------------------------------
    // Feature methods
    // ------------------------------------------------------------------

    /** Option 1: add a new item, or restock it if an item with that name already exists. */
    static void addItem() {
        String name = readName("Name: ");
        int quantity = readInt("Quantity: ");
        while (quantity < 0) {
            System.out.println("Quantity cannot be negative.");
            quantity = readInt("Quantity: ");
        }
        double price = readDouble("Price: ");
        while (price <= 0) {
            System.out.println("Price must be greater than 0.");
            price = readDouble("Price: ");
        }

        // Restock: same name already in stock -> increase its quantity
        Item existing = findByName(name);
        if (existing != null) {
            existing.quantity += quantity;
            existing.price = price;
            System.out.println("Item restocked. " + existing.id + " | " + existing.name
                    + " | Qty: " + existing.quantity + " | " + formatPrice(existing.price));
            saveToFile();
            return;
        }

        Item item = new Item(generateId(), name, quantity, price);
        items.add(item);
        System.out.println("Item added. ID: " + item.id + " | " + item.name
                + " | Qty: " + item.quantity + " | " + formatPrice(item.price));
        saveToFile();
    }

    /** Option 2: display every item in stock. */
    static void displayItems() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        System.out.println("ID   | Name | Qty | Price");
        System.out.println("-----------------------------");
        for (Item item : items) {
            System.out.println(item.id + " | " + item.name + " | " + item.quantity
                    + " | " + formatPrice(item.price));
        }
    }

    /** Option 3: linear search by item ID or by (part of) the item name. */
    static void searchItem() {
        String key = readText("Enter item ID or name to search: ").toLowerCase();
        boolean found = false;
        for (Item item : items) {
            if (item.id.toLowerCase().equals(key) || item.name.toLowerCase().contains(key)) {
                System.out.println("Found: " + item.id + " | " + item.name + " | Qty: "
                        + item.quantity + " | " + formatPrice(item.price));
                found = true;
            }
        }
        if (!found) {
            System.out.println("Item not found.");
        }
    }

    /** Option 4: sell items; quantity can never go below zero. */
    static void sellItem() {
        String id = readText("Enter item ID: ");
        Item item = findById(id);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }
        int quantity = readInt("Enter quantity to sell: ");
        while (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            quantity = readInt("Enter quantity to sell: ");
        }
        if (quantity > item.quantity) {
            System.out.println("Insufficient stock. Available: " + item.quantity);
            return;
        }
        item.quantity -= quantity;
        System.out.println("Stock updated. " + item.id + " | " + item.name + " | Qty: " + item.quantity);
        if (item.quantity == 0) {
            System.out.println("Item is now out of stock.");
        }
        saveToFile();
    }

    /** Option 5: list every item whose quantity is below 5. */
    static void lowStockReport() {
        boolean any = false;
        System.out.println("Low stock report (quantity below " + LOW_STOCK_LIMIT + "):");
        for (Item item : items) {
            if (item.quantity < LOW_STOCK_LIMIT) {
                System.out.println(item.id + " | " + item.name + " | Qty: " + item.quantity);
                any = true;
            }
        }
        if (!any) {
            System.out.println("No items are low on stock.");
        }
    }

    /** Option 6: sort by quantity (low to high) using selection sort on a copy of the list. */
    static void sortByQuantity() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        ArrayList<Item> sorted = new ArrayList<>(items);
        for (int i = 0; i < sorted.size() - 1; i++) {
            int min = i;
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).quantity < sorted.get(min).quantity) {
                    min = j;
                }
            }
            Item temp = sorted.get(i);
            sorted.set(i, sorted.get(min));
            sorted.set(min, temp);
        }
        System.out.println("Items sorted by quantity (low to high):");
        for (int i = 0; i < sorted.size(); i++) {
            Item item = sorted.get(i);
            System.out.println((i + 1) + ". " + item.id + " | " + item.name + " | " + item.quantity);
        }
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    /** Next ID = highest existing ID number + 1 (first ID is I101). */
    static String generateId() {
        int max = FIRST_ID_NUMBER - 1;
        for (Item item : items) {
            try {
                int number = Integer.parseInt(item.id.substring(1));
                if (number > max) {
                    max = number;
                }
            } catch (NumberFormatException e) {
                // ignore malformed IDs
            }
        }
        return "I" + (max + 1);
    }

    static Item findById(String id) {
        for (Item item : items) {
            if (item.id.equalsIgnoreCase(id.trim())) {
                return item;
            }
        }
        return null;
    }

    static Item findByName(String name) {
        for (Item item : items) {
            if (item.name.equalsIgnoreCase(name.trim())) {
                return item;
            }
        }
        return null;
    }

    /** Prints 40 instead of 40.0, and 12.50 for fractional prices. */
    static String formatPrice(double price) {
        if (price == Math.floor(price)) {
            return String.valueOf((long) price);
        }
        return String.format("%.2f", price);
    }

    // ------------------------------------------------------------------
    // File handling (format per line: ID,Name,Quantity,Price)
    // ------------------------------------------------------------------

    static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // first run: start with an empty inventory
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    continue; // skip a damaged line
                }
                try {
                    items.add(new Item(parts[0], parts[1],
                            Integer.parseInt(parts[2]), Double.parseDouble(parts[3])));
                } catch (NumberFormatException e) {
                    // skip a damaged line
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }

    static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Item item : items) {
                writer.write(item.id + "," + item.name + "," + item.quantity + "," + item.price);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not save " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Input helpers (re-prompt until the value is valid)
    // ------------------------------------------------------------------

    static String readText(String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) {      // input ended: save and quit cleanly
            saveToFile();
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    static String readName(String prompt) {
        String name = readText(prompt);
        while (name.isEmpty() || name.contains(",")) {
            System.out.println("Name cannot be empty or contain a comma.");
            name = readText(prompt);
        }
        return name;
    }

    static int readInt(String prompt) {
        while (true) {
            String text = readText(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            String text = readText(prompt);
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
