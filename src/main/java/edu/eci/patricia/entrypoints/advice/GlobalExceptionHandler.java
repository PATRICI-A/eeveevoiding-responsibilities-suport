package edu.eci.patricia.entrypoints.advice;

import edu.eci.patricia.domain.exceptions.InvalidCategoryException;
import edu.eci.patricia.domain.exceptions.MailtoGenerationException;
import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildBody(
                HttpStatus.NOT_FOUND, ex.getMessage()
        ));
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCategory(InvalidCategoryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBody(
                HttpStatus.BAD_REQUEST, ex.getMessage()
        ));
    }

    @ExceptionHandler(MailtoGenerationException.class)
    public ResponseEntity<Map<String, Object>> handleMailtoGeneration(MailtoGenerationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildBody(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildBody(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"
        ));
    }

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    status.value(),
                "error",     status.getReasonPhrase(),
                "message",   message
        );
    }
}