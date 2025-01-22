package com.example.product_service.services.ServicesImplementation;

import com.example.product_service.dtos.NewProduct;
import com.example.product_service.dtos.ProductDTO;
import com.example.product_service.models.Product;
import com.example.product_service.repositories.ProductRepository;
import com.example.product_service.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductDTO> getAllProducts() {

        List<ProductDTO> products;
        products = productRepository.findAll()
                .stream()
                .map(ProductDTO::new)
                .toList();

        return products;
    }

    @Override
    public ProductDTO createProduct(NewProduct newProduct) {
        Product product = new Product(newProduct.name(), newProduct.description(), newProduct.price(), newProduct.stock());

        product = productRepository.save(product);
        return new ProductDTO(product);
    }



    @Override
    public void updateProductStock(Long id, Integer stock) {
        Product product = getProductById(id);
        product.setStock(stock);
        productRepository.save(product);

    }
    //Exception handler is missing
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
