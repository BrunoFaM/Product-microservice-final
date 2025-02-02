package com.example.product_service.services;

import com.example.product_service.dtos.ProductLowStockDTO;
import com.example.product_service.dtos.ResponseReduceStock;

public interface MessageSenderService {

    void sendResponseReduceStockMessage(ResponseReduceStock responseReduceStock);

    public void sendLowStockMessage(ProductLowStockDTO productLowStockDTO);
}
