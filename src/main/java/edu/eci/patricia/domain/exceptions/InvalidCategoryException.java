package edu.eci.patricia.domain.exceptions;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String category) {
        super("Operation not allowed for category: " + category);
    }
}