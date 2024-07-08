package com.compass.application.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderItemDTO(
        @NotNull(message = "Product ID must not be null")
        @Positive(message = "Product ID must be a positive value")
        Long productId,

        @NotNull(message = "Quantity must not be null")
        @Positive(message = "Quantity must be a positive value")
        Integer quantity,

        @NotNull(message = "Discount must not be null")
        @PositiveOrZero(message = "Discount must be zero or a positive value")
        Double discount
) {
}
