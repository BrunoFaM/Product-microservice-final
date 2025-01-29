package com.example.product_service.controllers;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class RabbitTestController {

    @Autowired
    private AmqpTemplate amqpTemplate;


    @GetMapping
    public ResponseEntity<String> getMessage(){
        amqpTemplate.convertAndSend("testingExchange", "routing.key2", "***************************8");
        return ResponseEntity.ok("Id received");
    }

//    @GetMapping("/signal")
//    public ResponseEntity<String> getMessage(){
//        amqpTemplate.convertAndSend("testingExchange", "routing.key3", "Signal recived");
//        return ResponseEntity.ok("Message received");
//    }

}