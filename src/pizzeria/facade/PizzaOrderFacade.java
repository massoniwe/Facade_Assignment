package pizzeria.facade;

import pizzeria.subsystem.DeliveryService;
import pizzeria.subsystem.InventoryService;
import pizzeria.subsystem.KitchenService;
import pizzeria.subsystem.PaymentGateway;

public class PizzaOrderFacade {
    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;
    private final KitchenService kitchenService;
    private final DeliveryService deliveryService;

    public PizzaOrderFacade() {
        this.inventoryService = new InventoryService();
        this.paymentGateway = new PaymentGateway();
        this.kitchenService = new KitchenService();
        this.deliveryService = new DeliveryService();
    }

    public boolean placeOrder(String pizzaType, double price, String cardNumber, String address) {
        System.out.println("\n--- Starting order processing: Pizza '" + pizzaType + "' ---");

        // Step 1: Check and reserve stock
        if (!inventoryService.checkAndReserveIngredients(pizzaType)) {
            System.out.println("[Facade] Order canceled: Out of stock.");
            return false;
        }

        // Step 2: Payment
        if (!paymentGateway.processPayment(cardNumber, price)) {
            System.out.println("[Facade] Order canceled: Payment failed. Rolling back stock...");
            inventoryService.releaseIngredients(pizzaType);
            return false;
        }

        // Step 3: Kitchen
        int orderId = kitchenService.startCooking(pizzaType);

        // Step 4: Delivery
        String trackingCode = deliveryService.createDeliveryTask(orderId, address);

        System.out.println("[Facade] Success! Order #" + orderId + " completed. Tracking number: " + trackingCode);
        return true;
    }
}