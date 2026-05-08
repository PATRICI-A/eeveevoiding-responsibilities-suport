package edu.eci.patricia.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String id) {
        super("No se encontró el recurso de bienestar con id: " + id);
    }
}

