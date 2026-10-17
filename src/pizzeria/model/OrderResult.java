package pizzeria.model;

import java.math.BigDecimal;

public class OrderResult {
    private final boolean success;
    private final int orderId;
    private final String pizzaType;
    private final BigDecimal chargedAmount;
    private final String trackingCode;
    private final String courierName;
    private final int estimatedDeliveryMinutes;
    private final String message;

    private OrderResult(boolean success, int orderId, String pizzaType, BigDecimal chargedAmount,
                        String trackingCode, String courierName, int estimatedDeliveryMinutes, String message) {
        this.success = success;
        this.orderId = orderId;
        this.pizzaType = pizzaType;
        this.chargedAmount = chargedAmount;
        this.trackingCode = trackingCode;
        this.courierName = courierName;
        this.estimatedDeliveryMinutes = estimatedDeliveryMinutes;
        this.message = message;
    }

    public static OrderResult success(int orderId, String pizzaType, BigDecimal amount,
                                      String trackingCode, String courierName, int deliveryMinutes) {
        return new OrderResult(
                true,
                orderId,
                pizzaType,
                amount,
                trackingCode,
                courierName,
                deliveryMinutes,
                "Order #" + orderId + " (" + pizzaType + ") placed successfully! Estimated delivery: "
                        + deliveryMinutes + " min by courier " + courierName + "."
        );
    }

    public static OrderResult failure(String pizzaType, String failureReason) {
        return new OrderResult(
                false,
                -1,
                pizzaType,
                BigDecimal.ZERO,
                null,
                null,
                0,
                "Order for '" + pizzaType + "' failed: " + failureReason
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getPizzaType() {
        return pizzaType;
    }

    public BigDecimal getChargedAmount() {
        return chargedAmount;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public String getCourierName() {
        return courierName;
    }

    public int getEstimatedDeliveryMinutes() {
        return estimatedDeliveryMinutes;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (success) {
            return "[SUCCESS] Order #" + orderId + " | Pizza: " + pizzaType + " | Total: $" + chargedAmount
                    + " | Courier: " + courierName + " | Tracking: " + trackingCode
                    + " | ETA: " + estimatedDeliveryMinutes + " mins";
        } else {
            return "[FAILED] " + message;
        }
    }
}
