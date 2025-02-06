package com.example.product_service.services;

import com.example.product_service.dtos.*;
import com.example.product_service.exceptions.OrderErrorException;
import com.example.product_service.exceptions.ProductNotFoundException;
import com.example.product_service.models.Product;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts();

    ProductDTO createProduct(NewProduct newProduct);

    void updateProductStock(Long id ,Integer stock) throws ProductNotFoundException;

    Product getProductById(Long id) throws ProductNotFoundException;

    void makeOrder(List<ProductItem> products);

    void validateProductList(List<ProductItem> products);

    void validateStockAndReduce(ReduceStockRequest reduceStockRequest);

    List<ProductDetails> giveTheDetailsOfAll(List<ProductItem> products) throws ProductNotFoundException;
}
