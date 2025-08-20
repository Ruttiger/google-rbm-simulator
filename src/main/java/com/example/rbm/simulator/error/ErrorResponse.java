package com.example.rbm.simulator.error;

import org.springframework.http.HttpStatus;

public record ErrorResponse(Error error) {

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(new Error(status.getReasonPhrase(), status.value(), message));
    }

    public record Error(String status, int code, String message) {
    }
}
