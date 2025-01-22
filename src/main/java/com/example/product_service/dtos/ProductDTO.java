package com.example.product_service.dtos;

import com.example.product_service.models.Product;

public class ProductDTO {

    private Long id;

    private String name, description;

    private Double price;

    private Integer stock;

    public ProductDTO(Product product){
        id = product.getId();
        name = product.getName();
        description = product.getDescription();
        price = product.getPrice();
        stock = product.getStock();
    }

    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }
}
