package com.example.product_service.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record ProductItem(@Positive(message = "Can't be negative") Long productId,@Positive(message = "Can't be negative") Integer quantity) {
}
