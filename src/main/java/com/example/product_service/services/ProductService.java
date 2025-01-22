package com.example.product_service.services;

import com.example.product_service.dtos.NewProduct;
import com.example.product_service.dtos.ProductDTO;
import com.example.product_service.models.Product;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts();

    ProductDTO createProduct(NewProduct newProduct);

    void updateProductStock(Long id ,Integer stock);

    Product getProductById(Long id);
}
