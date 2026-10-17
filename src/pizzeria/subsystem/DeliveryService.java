package pizzeria.subsystem;

import pizzeria.exception.DeliveryUnavailableException;
import pizzeria.model.Courier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryService {
    public static final String OUT_OF_ZONE_KEYWORD = "Out of City";
    private static final int BASE_DELIVERY_MINUTES = 30;

    private final List<Courier> couriers = new ArrayList<>();
    private final Map<String, Courier> activeDeliveries = new HashMap<>();

    public DeliveryService() {
        initCouriers();
    }

    private void initCouriers() {
        couriers.add(new Courier("C1", "Alexey Smirnov", "+7-777-101-2233"));
        couriers.add(new Courier("C2", "Dmitry Ivanov", "+7-777-202-3344"));
        couriers.add(new Courier("C3", "Elena Petrova", "+7-777-303-4455"));
    }

    public synchronized DeliveryDetails createDeliveryTask(int orderId, String address) throws DeliveryUnavailableException {
        if (address == null || address.trim().isEmpty()) {
            throw new DeliveryUnavailableException("Delivery address cannot be empty.");
        }

        if (address.contains(OUT_OF_ZONE_KEYWORD)) {
            System.out.println("[Delivery] Error: Address '" + address + "' is outside pizzeria delivery radius.");
            throw new DeliveryUnavailableException("Address '" + address + "' is outside the delivery coverage zone.");
        }

        Courier availableCourier = couriers.stream()
                .filter(Courier::isAvailable)
                .findFirst()
                .orElse(null);

        if (availableCourier == null) {
            System.out.println("[Delivery] Error: All couriers are currently busy.");
            throw new DeliveryUnavailableException("No couriers available right now to fulfill delivery.");
        }

        availableCourier.setAvailable(false);
        String trackingCode = "TRK-" + orderId + "-" + availableCourier.getId();
        activeDeliveries.put(trackingCode, availableCourier);

        int estimatedMinutes = BASE_DELIVERY_MINUTES + (orderId % 10);

        System.out.println("[Delivery] Courier assigned: " + availableCourier);
        System.out.println("[Delivery] Address: " + address + " | Estimated time: " + estimatedMinutes + " mins.");
        System.out.println("[Delivery] Tracking code generated: " + trackingCode);

        return new DeliveryDetails(trackingCode, availableCourier.getName(), estimatedMinutes);
    }

    public synchronized void cancelDelivery(String trackingCode) {
        Courier assigned = activeDeliveries.remove(trackingCode);
        if (assigned != null) {
            assigned.setAvailable(true);
            System.out.println("[Delivery] Compensating rollback: Canceled delivery " + trackingCode
                    + ", courier " + assigned.getName() + " is now available.");
        }
    }

    public static class DeliveryDetails {
        private final String trackingCode;
        private final String courierName;
        private final int estimatedMinutes;

        public DeliveryDetails(String trackingCode, String courierName, int estimatedMinutes) {
            this.trackingCode = trackingCode;
            this.courierName = courierName;
            this.estimatedMinutes = estimatedMinutes;
        }

        public String getTrackingCode() {
            return trackingCode;
        }

        public String getCourierName() {
            return courierName;
        }

        public int getEstimatedMinutes() {
            return estimatedMinutes;
        }
    }
}