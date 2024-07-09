package com.compass.application.services.exceptions;

import java.io.Serial;

public class InsufficientStockException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientStockException(String message) {
        super(message);
    }

}
