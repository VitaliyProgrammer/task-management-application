package org.example.infrastructure.exception.authentication;

public class JwtException extends RuntimeException {

    public JwtException(String message) {
        super(message);
    }
}
