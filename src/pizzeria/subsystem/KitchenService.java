package pizzeria.subsystem;

public class KitchenService {
    public int startCooking(String pizzaType) {
        int orderId = (int) (Math.random() * 1000) + 100;
        System.out.println("[Kitchen] Order #" + orderId + " received. Baking pizza '" + pizzaType + "'...");
        System.out.println("[Kitchen] Pizza '" + pizzaType + "' is ready!");
        return orderId;
    }
}