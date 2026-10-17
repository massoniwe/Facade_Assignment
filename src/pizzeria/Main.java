package pizzeria;

import pizzeria.facade.PizzaOrderFacade;
import pizzeria.model.OrderRequest;
import pizzeria.model.OrderResult;
import pizzeria.subsystem.DeliveryService;
import pizzeria.subsystem.KitchenService;
import pizzeria.subsystem.PaymentGateway;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        PizzaOrderFacade pizzaFacade = new PizzaOrderFacade();

        System.out.println("===============================================================");
        System.out.println("          WELCOME TO PIZZERIA FACADE ORDER SYSTEM             ");
        System.out.println("===============================================================");

        // 1. Successful order (Happy Path)
        OrderRequest order1 = new OrderRequest(
                "Alice",
                "Pepperoni",
                new BigDecimal("12.99"),
                "4400123456789012",
                "Mangilik El Ave 55, Apt 12"
        );
        OrderResult result1 = pizzaFacade.placeOrder(order1);
        System.out.println("Client received: " + result1);

        // 2. Out-of-stock error (Ingredient 'Gorgonzola' has 0 stock)
        OrderRequest order2 = new OrderRequest(
                "Bob",
                "4 Cheese",
                new BigDecimal("14.50"),
                "4400123456789012",
                "Kenesary St 40"
        );
        OrderResult result2 = pizzaFacade.placeOrder(order2);
        System.out.println("Client received: " + result2);

        // 3. Payment error (Card starts with '0000') -> triggers inventory rollback
        OrderRequest order3 = new OrderRequest(
                "Charlie",
                "Margherita",
                new BigDecimal("10.50"),
                PaymentGateway.DECLINED_CARD_PREFIX + "123456789012",
                "Turan Ave 18"
        );
        OrderResult result3 = pizzaFacade.placeOrder(order3);
        System.out.println("Client received: " + result3);

        // 4. Kitchen error (Pizza 'Inferno' causes simulated oven breakdown)
        //    -> triggers refund + inventory rollback
        OrderRequest order4 = new OrderRequest(
                "Dave",
                KitchenService.FAILING_KITCHEN_PIZZA,
                new BigDecimal("15.00"),
                "4400998877665544",
                "Dostyk Ave 10"
        );
        OrderResult result4 = pizzaFacade.placeOrder(order4);
        System.out.println("Client received: " + result4);

        // 5. Delivery error (Address is outside coverage zone)
        //    -> triggers kitchen cancellation + refund + inventory rollback
        OrderRequest order5 = new OrderRequest(
                "Eva",
                "Pepperoni",
                new BigDecimal("12.99"),
                "4400998877665544",
                "Suburban Road 5, " + DeliveryService.OUT_OF_ZONE_KEYWORD
        );
        OrderResult result5 = pizzaFacade.placeOrder(order5);
        System.out.println("Client received: " + result5);

        System.out.println("\n===============================================================");
        System.out.println("                   ALL DEMO SCENARIOS COMPLETED                ");
        System.out.println("===============================================================");
    }
}