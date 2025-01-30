package com.example.product_service.services;


import com.example.product_service.dtos.NewProduct;
import com.example.product_service.dtos.ProductItem;
import com.example.product_service.dtos.ReduceStockRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class RabbitService {

    @Autowired
    private ProductService productService;



//this queue reduce the stock
    @RabbitListener(queues = "testingQueue1", concurrency = "1")
    public void listenerQueue1(ReduceStockRequest reduceStockRequest){
        System.out.println("LISTENER PRODUCT SERVICE");
        productService.validateStockAndReduce(reduceStockRequest);
        //send the success or the failure to the server
        System.out.println(reduceStockRequest);
        System.out.println("Mensaje de testingQueue: ");
    }




}
