package com.compass.application.dtos;

import jakarta.validation.constraints.Size;

import java.util.List;

public record SaleDTO(
        @Size(min = 1, message = "Order items must have at least 1 item")
        List<OrderItemDTO> orderItems
) {
}
