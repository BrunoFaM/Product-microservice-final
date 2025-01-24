package com.example.product_service.exceptions;

import java.util.Map;

public class OrderErrorException extends RuntimeException {
    public OrderErrorException(String message) {
        super(message);
    }

}
