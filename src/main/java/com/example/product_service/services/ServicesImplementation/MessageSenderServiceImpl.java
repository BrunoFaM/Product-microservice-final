package com.example.product_service.services.ServicesImplementation;

import com.example.product_service.dtos.ProductLowStockDTO;
import com.example.product_service.dtos.ResponseReduceStock;
import com.example.product_service.services.MessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderServiceImpl implements MessageSenderService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MessageSenderServiceImpl.class);

    @Override
    public void sendResponseReduceStockMessage(ResponseReduceStock responseReduceStock) {
        try {

            amqpTemplate.convertAndSend("exchange", "routing.key2", responseReduceStock);
        } catch (AmqpException e) {
            logger.warn("Something go wrong with Rabbit Server:", e.getMessage());
        }
    }

    @Override
    public void sendLowStockMessage(ProductLowStockDTO productLowStockDTO){
        try {
            amqpTemplate.convertAndSend("exchange", "routing.key5", productLowStockDTO);
        } catch (AmqpException e) {
            logger.warn("Something go wrong with Rabbit Server:", e.getMessage());
        }
    }

}
