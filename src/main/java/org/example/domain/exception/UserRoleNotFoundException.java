package org.example.domain.exception;

public class UserRoleNotFoundException extends RuntimeException {

    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
