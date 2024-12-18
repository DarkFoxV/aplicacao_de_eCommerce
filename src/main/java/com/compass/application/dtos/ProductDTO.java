package com.compass.application.dtos;

import com.compass.application.validations.ValidBoolean;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Price is required") @Positive(message = "Price must be positive or zero")
        Double price,

        @Positive(message = "Quantity must be positive or zero")
        Integer quantity,

        @Positive(message = "Product ID must be a positive value")
        Long id,

        @ValidBoolean(message = "Product availability must be specified (true or false)")
        Boolean enabled
) {
}
