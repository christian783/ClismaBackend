package io.app.clisma_backend.util;

public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException() {
        super();
    }

    public AlreadyExistsException(final String message) {
        super(message);
    }
}
