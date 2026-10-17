package pizzeria.exception;

public class OutOfStockException extends PizzeriaException {
    private final String missingIngredient;

    public OutOfStockException(String message, String missingIngredient) {
        super(message);
        this.missingIngredient = missingIngredient;
    }

    public String getMissingIngredient() {
        return missingIngredient;
    }
}
