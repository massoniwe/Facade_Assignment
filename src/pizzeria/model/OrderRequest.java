package pizzeria.model;

import java.math.BigDecimal;

public class OrderRequest {
    private final String customerName;
    private final String pizzaType;
    private final BigDecimal price;
    private final String cardNumber;
    private final String deliveryAddress;

    public OrderRequest(String customerName, String pizzaType, BigDecimal price, String cardNumber, String deliveryAddress) {
        this.customerName = customerName;
        this.pizzaType = pizzaType;
        this.price = price;
        this.cardNumber = cardNumber;
        this.deliveryAddress = deliveryAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPizzaType() {
        return pizzaType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }
}
