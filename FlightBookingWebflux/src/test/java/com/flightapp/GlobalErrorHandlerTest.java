package com.flightapp;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GlobalErrorHandlerTest {

    private GlobalErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new GlobalErrorHandler();
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Something went wrong");

        Mono responseMono = errorHandler.handleGenericException(ex);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    Map<String, Object> body = ((org.springframework.http.ResponseEntity<Map<String, Object>>) response).getBody();
                    return body.get("status").equals(500) &&
                           body.get("error").equals("Internal Server Error") &&
                           body.get("message").equals("Something went wrong");
                })
                .verifyComplete();
    }



    @Test
    void testHandleNotFoundException() {
        IllegalArgumentException ex = new IllegalArgumentException("User not found");

        Mono responseMono = errorHandler.handleNotFoundException(ex);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    Map<String, Object> body = ((org.springframework.http.ResponseEntity<Map<String, Object>>) response).getBody();
                    return body.get("status").equals(404) &&
                           body.get("error").equals("Not Found") &&
                           body.get("message").equals("User not found");
                })
                .verifyComplete();
    }
}
