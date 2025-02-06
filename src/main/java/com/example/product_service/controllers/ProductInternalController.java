package com.example.product_service.controllers;

import com.example.product_service.dtos.ProductItem;
import com.example.product_service.exceptions.ProductNotFoundException;
import com.example.product_service.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductInternalController {

    @Autowired
    private ProductService productService;

    @PostMapping("/order/validation")
    public ResponseEntity<?> validateOrderReturnErrorMap(@Valid @RequestBody List<ProductItem> products){

        productService.validateProductList(products);
        return new ResponseEntity<>( HttpStatus.OK);
    }
    //for order details
    @PostMapping("/details")
    public ResponseEntity<?> returnDetailsOfProducts(@Valid @RequestBody List<ProductItem> productItems) throws ProductNotFoundException {
        return new ResponseEntity<>(productService.giveTheDetailsOfAll(productItems), HttpStatus.OK);
    }

}
