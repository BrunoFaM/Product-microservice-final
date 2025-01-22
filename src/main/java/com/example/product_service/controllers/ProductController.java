package com.example.product_service.controllers;

import com.example.product_service.dtos.NewProduct;
import com.example.product_service.dtos.ProductDTO;
import com.example.product_service.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getProducts(){

        List<ProductDTO> products = productService.getAllProducts();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductStock(@PathVariable Long id, @RequestBody int stock){
        productService.updateProductStock(id, stock);

        return new ResponseEntity<>("The product was updated", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postProduct(@RequestBody NewProduct newProduct){
        ProductDTO savedProduct =productService.createProduct(newProduct);

        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
}
