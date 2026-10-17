package pizzeria;

import pizzeria.facade.PizzaOrderFacade;

public class Main {
    public static void main(String[] args) {
        PizzaOrderFacade pizzaFacade = new PizzaOrderFacade();

        // 1. Successful order
        pizzaFacade.placeOrder("Pepperoni", 12.99, "4400123456789012", "Mangilik El Ave 55");

        // 2. Payment error (card starting with 0000) - triggers rollback
        pizzaFacade.placeOrder("Margherita", 10.50, "0000000000000000", "Turan Ave 18");

        // 3. Stock error ("4 Cheese" is out of stock)
        pizzaFacade.placeOrder("4 Cheese", 14.00, "4400123456789012", "Kenesary St 40");
    }
}