package com.flightapp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Map<String, String>> handleValidationExceptions(WebExchangeBindException exception) {

        String combinedErrors = exception.getFieldErrors()
            .stream()
            .map(error -> error.getDefaultMessage())  
            .distinct()                              
            .reduce((msg1, msg2) -> msg1 + ", " + msg2)
            .orElse("Validation error");

        Map<String, String> response = new HashMap<>();
        response.put("error", combinedErrors);  

        return Mono.just(response);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<Map<String, String>> handleDuplicateKeyException(DuplicateKeyException ex) {

        Map<String, String> errors = new HashMap<>();
        String message = ex.getMessage();

        if (message.contains("email")) {
            errors.put("error", "Email already exists");
        } else {
            errors.put("error", "Duplicate key error");
        }

        return Mono.just(errors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getReason());
        return Mono.just(error);
    }

    @ExceptionHandler(Exception.class)
    public Mono<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        return Mono.just(error);
    }
}
