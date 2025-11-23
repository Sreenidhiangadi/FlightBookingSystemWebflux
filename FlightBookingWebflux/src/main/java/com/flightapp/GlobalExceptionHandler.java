package com.flightapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Constant to avoid repeating the "timestamp" literal
    private static final String TIMESTAMP = "timestamp";

    private Mono<ResponseEntity<Map<String, Object>>> buildResponse(
            HttpStatus status, String error, String message, Map<String, Object> additionalFields) {

        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        if (additionalFields != null) {
            body.putAll(additionalFields);
        }

        return Mono.just(ResponseEntity.status(status).body(body));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> additionalFields = new HashMap<>();
        Map<String, String> fieldErrors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        additionalFields.put("fieldErrors", fieldErrors);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Invalid input",
                additionalFields
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNotFoundException(IllegalArgumentException ex) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                null
        );
    }
}
