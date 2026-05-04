package org.example.domain.exception;

public class LabelNotFoundException extends RuntimeException {

    public LabelNotFoundException(String message) {
        super(message);
    }
}
