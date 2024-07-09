package com.compass.application.services.exceptions;

import java.io.Serial;

public class ProductNotAvailableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductNotAvailableException(String message) {
        super(message);
    }

}
