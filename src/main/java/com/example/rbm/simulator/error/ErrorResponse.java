package com.example.rbm.simulator.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponse {
    private final Error error;

    public ErrorResponse(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(new Error(status.getReasonPhrase(), status.value(), message));
    }

    public static ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(of(status, message));
    }

    public record Error(String status, int code, String message) {
    }
}
