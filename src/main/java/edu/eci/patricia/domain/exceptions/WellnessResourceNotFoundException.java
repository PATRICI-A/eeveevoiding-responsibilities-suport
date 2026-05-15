package edu.eci.patricia.domain.exceptions;

public class WellnessResourceNotFoundException extends RuntimeException {
    public WellnessResourceNotFoundException(String id) {
        super("Wellness resource not found with id: " + id);
    }
}