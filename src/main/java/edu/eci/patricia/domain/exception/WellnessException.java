package edu.eci.patricia.domain.exception;

/**
 * General-purpose exception for business rule violations in the wellness domain.
 */
public class WellnessException extends RuntimeException {

    /**
     * Constructs a new WellnessException with the given message.
     *
     * @param message description of the business rule violation
     */
    public WellnessException(String message) {
        super(message);
    }

    /**
     * Constructs a new WellnessException with a message and a cause.
     *
     * @param message description of the business rule violation
     * @param cause   the underlying exception
     */
    public WellnessException(String message, Throwable cause) {
        super(message, cause);
    }
}
