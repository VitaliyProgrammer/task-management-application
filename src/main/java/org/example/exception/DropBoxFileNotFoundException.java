package org.example.exception;

public class DropBoxFileNotFoundException extends RuntimeException {

    public DropBoxFileNotFoundException(String message) {
        super(message);
    }
}
