package pizzeria.exception;

public class PizzeriaException extends Exception {
    public PizzeriaException(String message) {
        super(message);
    }

    public PizzeriaException(String message, Throwable cause) {
        super(message, cause);
    }
}
