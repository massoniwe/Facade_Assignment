package pizzeria.subsystem;

public class PaymentGateway {
    public boolean processPayment(String cardNumber, double amount) {
        if (cardNumber == null || cardNumber.startsWith("0000")) {
            System.out.println("[Payment] Error: Payment of $" + amount + " declined (invalid card number).");
            return false;
        }
        System.out.println("[Payment] Payment of $" + amount + " with card " + maskCard(cardNumber) + " successful.");
        return true;
    }

    private String maskCard(String cardNumber) {
        if (cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}