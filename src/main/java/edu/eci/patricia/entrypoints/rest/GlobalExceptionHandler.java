package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.exception.WellnessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers in the wellness support service.
 * Translates domain exceptions into standardised HTTP error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ResourceNotFoundException} and returns HTTP 404.
     *
     * @param ex the exception thrown when a requested resource does not exist
     * @return a structured 404 response body
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Handles {@link WellnessException} and returns HTTP 403 for access-control violations
     * or HTTP 400 for general business rule violations.
     *
     * @param ex the wellness domain exception
     * @return a structured 403 or 400 response body
     */
    @ExceptionHandler(WellnessException.class)
    public ResponseEntity<Map<String, Object>> handleWellnessException(WellnessException ex) {
        log.warn("Wellness business rule violation: {}", ex.getMessage());
        HttpStatus status = ex.getMessage().startsWith("Access denied") ? HttpStatus.FORBIDDEN : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(buildBody(status, ex.getMessage()));
    }

    /**
     * Handles bean validation errors ({@link MethodArgumentNotValidException}) and returns HTTP 400.
     * The response includes a map of field names to their validation error messages.
     *
     * @param ex the validation exception containing field errors
     * @return a 400 response body listing all validation failures
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (first, second) -> first
                ));

        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("fieldErrors", fieldErrors);
        log.warn("Validation errors: {}", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Catch-all handler for any unhandled exception, returns HTTP 500.
     *
     * @param ex the unexpected exception
     * @return a generic 500 response body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later."));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
