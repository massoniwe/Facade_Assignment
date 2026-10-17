package pizzeria.subsystem;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> stock = new HashMap<>();

    public InventoryService() {
        stock.put("Pepperoni", 10);
        stock.put("Margherita", 5);
        stock.put("4 Cheese", 0); // Out of stock for testing errors
    }

    public boolean checkAndReserveIngredients(String pizzaType) {
        int count = stock.getOrDefault(pizzaType, 0);
        if (count > 0) {
            stock.put(pizzaType, count - 1);
            System.out.println("[Inventory] Ingredients for pizza '" + pizzaType + "' reserved.");
            return true;
        }
        System.out.println("[Inventory] Error: Not enough ingredients for pizza '" + pizzaType + "'.");
        return false;
    }

    public void releaseIngredients(String pizzaType) {
        stock.put(pizzaType, stock.getOrDefault(pizzaType, 0) + 1);
        System.out.println("[Inventory] Ingredients for pizza '" + pizzaType + "' returned to stock.");
    }
}