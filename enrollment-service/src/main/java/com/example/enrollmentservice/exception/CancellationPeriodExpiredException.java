package com.example.enrollmentservice.exception;

public class CancellationPeriodExpiredException extends RuntimeException {

    public CancellationPeriodExpiredException(String message) {
        super(message);
    }
}
