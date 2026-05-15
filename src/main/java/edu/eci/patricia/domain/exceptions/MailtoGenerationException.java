package edu.eci.patricia.domain.exceptions;

public class MailtoGenerationException extends RuntimeException {
    public MailtoGenerationException(String resourceId, Throwable cause) {
        super("Failed to generate mailto for resource: " + resourceId, cause);
    }
}