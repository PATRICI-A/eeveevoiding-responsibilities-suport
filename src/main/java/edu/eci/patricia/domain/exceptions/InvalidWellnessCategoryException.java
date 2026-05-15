package edu.eci.patricia.domain.exceptions;

public class InvalidWellnessCategoryException extends RuntimeException {
    public InvalidWellnessCategoryException(String resourceId) {
        super("Appointment mailto is only available for MENTAL_HEALTH resources. Resource id: " + resourceId);
    }
}