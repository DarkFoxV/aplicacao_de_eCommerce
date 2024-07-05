package com.compass.application.dtos;

import com.compass.application.validations.PositiveValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductDTO(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Price is required") @PositiveValue(message = "Price must be positive or zero") Double price,
        Long id
        ) {
}
