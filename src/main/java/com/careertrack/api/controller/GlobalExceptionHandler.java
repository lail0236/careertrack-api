
package com.careertrack.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError :
                exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ApiError body = new ApiError(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "One or more fields are invalid.",
                errors
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatusException(
            ResponseStatusException exception) {

        HttpStatus status = HttpStatus.valueOf(
                exception.getStatusCode().value()
        );

        ApiError body = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getReason(),
                Map.of()
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableRequest(
            HttpMessageNotReadableException exception) {

        ApiError body = new ApiError(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "The request body is invalid or contains an incorrectly formatted value.",
                Map.of()
        );

        return ResponseEntity.badRequest().body(body);
    }
}
