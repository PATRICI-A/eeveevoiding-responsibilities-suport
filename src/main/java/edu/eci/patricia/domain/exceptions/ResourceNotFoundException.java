package edu.eci.patricia.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceId) {
        super("Wellness resource not found with id: " + resourceId);
    }
}