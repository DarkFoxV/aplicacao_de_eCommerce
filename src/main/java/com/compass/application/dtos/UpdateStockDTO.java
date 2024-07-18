package com.compass.application.dtos;

import jakarta.validation.constraints.NotNull;

public record UpdateStockDTO(
        @NotNull(message = "quantity is required")
        Integer quantity
) {
}
