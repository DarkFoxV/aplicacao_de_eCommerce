package com.compass.application.services.exceptions;

import java.io.Serial;

public class ObjectNotAvailableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ObjectNotAvailableException(String message) {
        super(message);
    }

}
