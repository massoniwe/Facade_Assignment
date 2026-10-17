package pizzeria.subsystem;

import pizzeria.exception.PaymentFailedException;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentGateway {
    public static final String DECLINED_CARD_PREFIX = "0000";
    private static final int MASK_UNMASKED_CHARS = 4;

    public String processPayment(String cardNumber, BigDecimal amount) throws PaymentFailedException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentFailedException("Invalid payment amount: " + amount);
        }

        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new PaymentFailedException("Credit card number cannot be empty.");
        }

        if (cardNumber.startsWith(DECLINED_CARD_PREFIX)) {
            System.out.println("[Payment] Error: Transaction declined for card " + maskCard(cardNumber) + " (amount: $" + amount + ").");
            throw new PaymentFailedException("Payment declined by issuing bank (test card prefix " + DECLINED_CARD_PREFIX + ").");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("[Payment] Payment of $" + amount + " successful with card " + maskCard(cardNumber)
                + ". Transaction ID: " + transactionId);
        return transactionId;
    }

    public void refund(String transactionId, BigDecimal amount) {
        if (transactionId == null) {
            return;
        }
        System.out.println("[Payment] Compensating rollback: Refunded $" + amount + " for transaction " + transactionId + ".");
    }

    private String maskCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < MASK_UNMASKED_CHARS) {
            return "****";
        }
        String lastDigits = cardNumber.substring(cardNumber.length() - MASK_UNMASKED_CHARS);
        return "**** **** **** " + lastDigits;
    }
}