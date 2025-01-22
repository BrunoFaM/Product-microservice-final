package com.example.product_service.exceptions;

public class ProductNotFoundException extends Exception {

  private static final String message = "Order not found";

    public ProductNotFoundException() {
        super(message);
    }
}
