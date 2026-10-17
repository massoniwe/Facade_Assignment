package pizzeria.subsystem;

import pizzeria.exception.KitchenException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class KitchenService {
    public static final String FAILING_KITCHEN_PIZZA = "Inferno";
    private static final int MAX_ACTIVE_ORDERS = 5;

    private final AtomicInteger orderCounter = new AtomicInteger(100);
    private final Set<Integer> activeOrders = new HashSet<>();

    public synchronized int startCooking(String pizzaType) throws KitchenException {
        if (FAILING_KITCHEN_PIZZA.equalsIgnoreCase(pizzaType)) {
            System.out.println("[Kitchen] Error: Oven breakdown occurred while preparing '" + pizzaType + "'!");
            throw new KitchenException("Kitchen oven malfunction during pizza preparation.");
        }

        if (activeOrders.size() >= MAX_ACTIVE_ORDERS) {
            System.out.println("[Kitchen] Error: Kitchen capacity reached (" + activeOrders.size() + " active orders).");
            throw new KitchenException("Kitchen is currently overloaded. Please try again shortly.");
        }

        int orderId = orderCounter.incrementAndGet();
        activeOrders.add(orderId);

        System.out.println("[Kitchen] Order #" + orderId + " accepted.");
        System.out.println("[Kitchen] Step 1: Stretching dough and applying sauce/toppings for '" + pizzaType + "'.");
        System.out.println("[Kitchen] Step 2: Baking in stone oven at 450°C...");
        System.out.println("[Kitchen] Step 3: Quality check passed. Slicing and packaging pizza '" + pizzaType + "'.");

        return orderId;
    }

    public synchronized void cancelCooking(int orderId) {
        if (activeOrders.remove(orderId)) {
            System.out.println("[Kitchen] Compensating rollback: Canceled cooking order #" + orderId + ".");
        } else {
            System.out.println("[Kitchen] Order #" + orderId + " was already completed or not found.");
        }
    }

    public synchronized int getActiveOrdersCount() {
        return activeOrders.size();
    }
}