package com.example.rbm.simulator.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ErrorResponse.build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(WebExchangeBindException ex) {
        String message = ex.getAllErrors().isEmpty() ? "Validation failed" : ex.getAllErrors().get(0).getDefaultMessage();
        return ErrorResponse.build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleInputException(ServerWebInputException ex) {
        return ErrorResponse.build(HttpStatus.BAD_REQUEST, ex.getReason());
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMedia(UnsupportedMediaTypeStatusException ex) {
        return ErrorResponse.build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ErrorResponse.build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }
}
