package com.flightapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Something went wrong");

        Mono<ResponseEntity<Map<String, Object>>> responseMono = exceptionHandler.handleGenericException(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals(500, body.get("status"));
                    assertEquals("Internal Server Error", body.get("error"));
                    assertEquals("Something went wrong", body.get("message"));
                    assertNotNull(body.get("timestamp"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleValidationException() {
        // Mock WebExchangeBindException
        WebExchangeBindException ex = mock(WebExchangeBindException.class);

        FieldError fieldError = new FieldError("objectName", "field1", "must not be null");
        when(ex.getFieldErrors()).thenReturn(List.of(fieldError));

        Mono<ResponseEntity<Map<String, Object>>> responseMono = exceptionHandler.handleValidationException(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals(400, body.get("status"));
                    assertEquals("Validation Error", body.get("error"));
                    assertEquals("Invalid input", body.get("message"));

                    @SuppressWarnings("unchecked")
                    Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
                    assertNotNull(fieldErrors);
                    assertEquals(1, fieldErrors.size());
                    assertEquals("must not be null", fieldErrors.get("field1"));

                    assertNotNull(body.get("timestamp"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleNotFoundException() {
        IllegalArgumentException ex = new IllegalArgumentException("Data not found");

        Mono<ResponseEntity<Map<String, Object>>> responseMono = exceptionHandler.handleNotFoundException(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals(404, body.get("status"));
                    assertEquals("Not Found", body.get("error"));
                    assertEquals("Data not found", body.get("message"));
                    assertNotNull(body.get("timestamp"));
                })
                .verifyComplete();
    }
}
