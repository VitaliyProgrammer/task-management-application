package org.example.domain.exception;

public class LabelAlreadyExistsException extends RuntimeException {

    public LabelAlreadyExistsException(String message) {
        super(message);
    }
}
