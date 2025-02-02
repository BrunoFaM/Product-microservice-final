package com.example.product_service.dtos;

public record ProductLowStockDTO(Long productId, String name, Integer stock) {
}
