package com.compass.application.services.exceptions;

import java.io.Serial;

public class ProductInSaleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductInSaleException(String message) {
        super(message);
    }

}
