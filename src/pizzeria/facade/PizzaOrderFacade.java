package pizzeria.facade;

import pizzeria.exception.DeliveryUnavailableException;
import pizzeria.exception.KitchenException;
import pizzeria.exception.OutOfStockException;
import pizzeria.exception.PaymentFailedException;
import pizzeria.model.OrderRequest;
import pizzeria.model.OrderResult;
import pizzeria.subsystem.DeliveryService;
import pizzeria.subsystem.DeliveryService.DeliveryDetails;
import pizzeria.subsystem.InventoryService;
import pizzeria.subsystem.KitchenService;
import pizzeria.subsystem.PaymentGateway;

import java.math.BigDecimal;

public class PizzaOrderFacade {
    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;
    private final KitchenService kitchenService;
    private final DeliveryService deliveryService;

    public PizzaOrderFacade() {
        this(new InventoryService(), new PaymentGateway(), new KitchenService(), new DeliveryService());
    }

    public PizzaOrderFacade(InventoryService inventoryService,
                            PaymentGateway paymentGateway,
                            KitchenService kitchenService,
                            DeliveryService deliveryService) {
        this.inventoryService = inventoryService;
        this.paymentGateway = paymentGateway;
        this.kitchenService = kitchenService;
        this.deliveryService = deliveryService;
    }

    public OrderResult placeOrder(OrderRequest request) {
        if (request == null) {
            return OrderResult.failure("N/A", "Order request cannot be null.");
        }

        String pizzaType = request.getPizzaType();
        BigDecimal price = request.getPrice();
        String customer = request.getCustomerName();

        System.out.println("\n=======================================================");
        System.out.println("Processing order: '" + pizzaType + "' for " + customer + " ($" + price + ")");
        System.out.println("=======================================================");

        boolean ingredientsReserved = false;
        String transactionId = null;
        Integer orderId = null;

        try {
            // Step 1: Reserve ingredients
            System.out.println("[Step 1/4] Checking and reserving ingredients...");
            inventoryService.checkAndReserveIngredients(pizzaType);
            ingredientsReserved = true;

            // Step 2: Process payment
            System.out.println("[Step 2/4] Processing payment...");
            transactionId = paymentGateway.processPayment(request.getCardNumber(), price);

            // Step 3: Kitchen cooking
            System.out.println("[Step 3/4] Sending order to kitchen...");
            orderId = kitchenService.startCooking(pizzaType);

            // Step 4: Dispatch delivery
            System.out.println("[Step 4/4] Scheduling delivery...");
            DeliveryDetails delivery = deliveryService.createDeliveryTask(orderId, request.getDeliveryAddress());

            System.out.println("[Facade] Order #" + orderId + " successfully completed!");
            return OrderResult.success(
                    orderId,
                    pizzaType,
                    price,
                    delivery.getTrackingCode(),
                    delivery.getCourierName(),
                    delivery.getEstimatedMinutes()
            );

        } catch (OutOfStockException e) {
            System.out.println("[Facade] FAILURE at Inventory step: " + e.getMessage());
            return OrderResult.failure(pizzaType, "Out of stock - " + e.getMessage());

        } catch (PaymentFailedException e) {
            System.out.println("[Facade] FAILURE at Payment step: " + e.getMessage());
            compensate(ingredientsReserved, pizzaType, null, price, null);
            return OrderResult.failure(pizzaType, "Payment failed - " + e.getMessage());

        } catch (KitchenException e) {
            System.out.println("[Facade] FAILURE at Kitchen step: " + e.getMessage());
            compensate(ingredientsReserved, pizzaType, transactionId, price, null);
            return OrderResult.failure(pizzaType, "Kitchen error - " + e.getMessage());

        } catch (DeliveryUnavailableException e) {
            System.out.println("[Facade] FAILURE at Delivery step: " + e.getMessage());
            compensate(ingredientsReserved, pizzaType, transactionId, price, orderId);
            return OrderResult.failure(pizzaType, "Delivery error - " + e.getMessage());

        } catch (Exception e) {
            System.out.println("[Facade] UNEXPECTED ERROR: " + e.getMessage());
            compensate(ingredientsReserved, pizzaType, transactionId, price, orderId);
            return OrderResult.failure(pizzaType, "System error: " + e.getMessage());
        }
    }

    /**
     * Executes compensating transactions (rollback) in reverse order of operation execution.
     */
    private void compensate(boolean rollbackInventory, String pizzaType,
                            String transactionId, BigDecimal price, Integer orderId) {
        System.out.println("[Facade] --- INITIATING COMPENSATING TRANSACTIONS (ROLLBACK) ---");

        if (orderId != null) {
            kitchenService.cancelCooking(orderId);
        }

        if (transactionId != null) {
            paymentGateway.refund(transactionId, price);
        }

        if (rollbackInventory) {
            inventoryService.releaseIngredients(pizzaType);
        }

        System.out.println("[Facade] --- ROLLBACK COMPLETED SUCCESSFULLY ---");
    }

    // Convenience method for simple invocations
    public OrderResult placeOrder(String customerName, String pizzaType, BigDecimal price,
                                  String cardNumber, String address) {
        return placeOrder(new OrderRequest(customerName, pizzaType, price, cardNumber, address));
    }
}