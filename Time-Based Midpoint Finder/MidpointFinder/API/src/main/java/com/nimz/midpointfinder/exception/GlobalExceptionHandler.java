package com.nimz.midpointfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RoutingException.class)
    public ResponseEntity<Map<String, Object>> handleRoutingException(RoutingException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Routing Error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
