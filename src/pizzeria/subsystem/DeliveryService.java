package pizzeria.subsystem;

public class DeliveryService {
    public String createDeliveryTask(int orderId, String address) {
        String trackingCode = "TRK-" + orderId + "-" + (int) (Math.random() * 8999 + 1000);
        System.out.println("[Delivery] Courier assigned for order #" + orderId + ".");
        System.out.println("[Delivery] Delivery address: " + address + ". Tracking code: " + trackingCode);
        return trackingCode;
    }
}