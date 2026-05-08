package edu.eci.patricia.domain.exceptions;

public class InvalidCategoryForMailtoException extends RuntimeException {
    public InvalidCategoryForMailtoException(String id) {
        super("El recurso con id " + id +
                " no es de categoría MENTAL_HEALTH. El enlace de cita solo aplica para recursos de salud.");
    }
}

