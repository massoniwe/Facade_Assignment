package pizzeria.subsystem;

import pizzeria.exception.OutOfStockException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    // Ingredient constants to avoid magic strings
    public static final String INGREDIENT_DOUGH = "Dough";
    public static final String INGREDIENT_TOMATO_SAUCE = "Tomato Sauce";
    public static final String INGREDIENT_MOZZARELLA = "Mozzarella";
    public static final String INGREDIENT_PEPPERONI = "Pepperoni";
    public static final String INGREDIENT_BASIL = "Basil";
    public static final String INGREDIENT_PARMESAN = "Parmesan";
    public static final String INGREDIENT_GORGONZOLA = "Gorgonzola";

    // Stock levels of individual raw ingredients
    private final Map<String, Integer> stock = new HashMap<>();

    // Recipes mapping pizza name to its required ingredients
    private final Map<String, List<String>> recipes = new HashMap<>();

    public InventoryService() {
        initStock();
        initRecipes();
    }

    private void initStock() {
        stock.put(INGREDIENT_DOUGH, 15);
        stock.put(INGREDIENT_TOMATO_SAUCE, 15);
        stock.put(INGREDIENT_MOZZARELLA, 10);
        stock.put(INGREDIENT_PEPPERONI, 8);
        stock.put(INGREDIENT_BASIL, 6);
        stock.put(INGREDIENT_PARMESAN, 5);
        // Gorgonzola is intentionally set to 0 to simulate out-of-stock for '4 Cheese' pizza
        stock.put(INGREDIENT_GORGONZOLA, 0);
    }

    private void initRecipes() {
        recipes.put("Pepperoni", List.of(
                INGREDIENT_DOUGH,
                INGREDIENT_TOMATO_SAUCE,
                INGREDIENT_MOZZARELLA,
                INGREDIENT_PEPPERONI
        ));
        recipes.put("Margherita", List.of(
                INGREDIENT_DOUGH,
                INGREDIENT_TOMATO_SAUCE,
                INGREDIENT_MOZZARELLA,
                INGREDIENT_BASIL
        ));
        recipes.put("4 Cheese", List.of(
                INGREDIENT_DOUGH,
                INGREDIENT_MOZZARELLA,
                INGREDIENT_PARMESAN,
                INGREDIENT_GORGONZOLA
        ));
        recipes.put("Inferno", List.of(
                INGREDIENT_DOUGH,
                INGREDIENT_TOMATO_SAUCE,
                INGREDIENT_MOZZARELLA,
                INGREDIENT_PEPPERONI
        ));
    }

    public synchronized void checkAndReserveIngredients(String pizzaType) throws OutOfStockException {
        List<String> requiredIngredients = recipes.get(pizzaType);
        if (requiredIngredients == null) {
            throw new OutOfStockException("Unknown pizza type: '" + pizzaType + "'. Recipe not found.", "Unknown");
        }

        // Verify that all required ingredients are in stock
        for (String ingredient : requiredIngredients) {
            int available = stock.getOrDefault(ingredient, 0);
            if (available <= 0) {
                System.out.println("[Inventory] Error: Ingredient '" + ingredient + "' is out of stock for pizza '" + pizzaType + "'.");
                throw new OutOfStockException("Not enough '" + ingredient + "' for pizza '" + pizzaType + "'", ingredient);
            }
        }

        // Reserve ingredients
        for (String ingredient : requiredIngredients) {
            stock.put(ingredient, stock.get(ingredient) - 1);
        }
        System.out.println("[Inventory] Ingredients reserved for '" + pizzaType + "': " + requiredIngredients);
    }

    public synchronized void releaseIngredients(String pizzaType) {
        List<String> requiredIngredients = recipes.get(pizzaType);
        if (requiredIngredients == null) {
            return;
        }

        for (String ingredient : requiredIngredients) {
            stock.put(ingredient, stock.getOrDefault(ingredient, 0) + 1);
        }
        System.out.println("[Inventory] Ingredients for pizza '" + pizzaType + "' returned to stock: " + requiredIngredients);
    }
}