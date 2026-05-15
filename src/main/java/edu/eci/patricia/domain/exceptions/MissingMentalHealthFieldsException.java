package edu.eci.patricia.domain.exceptions;

public class MissingMentalHealthFieldsException extends RuntimeException {
    public MissingMentalHealthFieldsException() {
        super("MENTAL_HEALTH resources must have appointmentEmail and psychologistName");
    }
}