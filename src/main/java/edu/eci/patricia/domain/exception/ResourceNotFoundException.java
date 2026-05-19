package edu.eci.patricia.domain.exception;

/**
 * Exception thrown when a requested domain resource does not exist in the system.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the given message.
     *
     * @param message human-readable description of which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with a message and a cause.
     *
     * @param message human-readable description of which resource was not found
     * @param cause   the underlying exception that triggered this one
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
