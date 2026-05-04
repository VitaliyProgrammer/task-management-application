package org.example.infrastructure.exception.integration;

public class DropBoxFileNotFoundException extends RuntimeException {

    public DropBoxFileNotFoundException(String message) {
        super(message);
    }
}
