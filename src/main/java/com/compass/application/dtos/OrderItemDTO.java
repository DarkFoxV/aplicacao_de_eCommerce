package com.compass.application.dtos;

public record OrderItemDTO (
        Long productId,
        Integer quantity,
        Double discount
){
}
