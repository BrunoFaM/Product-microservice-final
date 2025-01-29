package com.example.product_service.dtos;

import java.util.List;

public record ReduceStockRequest(Long orderId, List<ProductItem> products) {
}
