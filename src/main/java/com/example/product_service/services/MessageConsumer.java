package com.example.product_service.services;

import com.example.product_service.dtos.ReduceStockRequest;

public interface MessageConsumer {

    void listenerReduceStock(ReduceStockRequest reduceStockRequest);
}
