package org.example.infrastructure.exception.authentication;

public class ExpiredJwtException extends RuntimeException {

    public ExpiredJwtException(String message) {
        super(message);
    }
}
