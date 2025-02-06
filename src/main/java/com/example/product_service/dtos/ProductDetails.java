package com.example.product_service.dtos;

public record ProductDetails(Long id, String name, Integer quantity, Double price, Double subtotal) {
}
