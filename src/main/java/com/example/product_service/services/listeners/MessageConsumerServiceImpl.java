package com.example.product_service.services.listeners;


import com.example.product_service.dtos.ReduceStockRequest;
import com.example.product_service.services.MessageConsumer;
import com.example.product_service.services.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumerServiceImpl implements MessageConsumer {

    @Autowired
    private ProductService productService;


    @RabbitListener(queues = "reduceStockQueue", concurrency = "1")
    public void listenerReduceStock(ReduceStockRequest reduceStockRequest){

        productService.validateStockAndReduce(reduceStockRequest);

    }
}
