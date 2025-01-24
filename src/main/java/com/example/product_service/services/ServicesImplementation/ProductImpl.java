package com.example.product_service.services.ServicesImplementation;

import com.example.product_service.dtos.NewProduct;
import com.example.product_service.dtos.ProductDTO;
import com.example.product_service.dtos.ProductItem;
import com.example.product_service.exceptions.OrderErrorException;
import com.example.product_service.exceptions.ProductNotFoundException;
import com.example.product_service.models.Product;
import com.example.product_service.repositories.ProductRepository;
import com.example.product_service.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProductImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    private Map<String, String> orderErrors;

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
    public void updateProductStock(Long id, Integer stock) throws ProductNotFoundException {
        Product product = getProductById(id);
        product.setStock(stock);
        productRepository.save(product);

    }
    //Exception handler is missing
    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException());
    }


    private void commitOrder(List<ProductItem> products){
        for(ProductItem productItem : products){
            Product product = productRepository.findById(productItem.productId()).orElse(null);
            //extra validation, in case the same product was sent two times
            Integer newStock = product.getStock() - productItem.quantity();
            if(newStock >= 0){
                product.setStock(newStock);
                productRepository.save(product);
            }

        }
    }

    private void addNotFoundError(Long productId){
        orderErrors.put( "Product " + productId , "not found");
    }
    private void addNotStockError(Long productId, Integer actualStock){
        orderErrors.put("Product " + productId, "only "+  actualStock + " units left");
    }

    private Map<String, String> orderValidation(List<ProductItem> products){
        //boolean flag = true;

        //i build a map of all the errors in the order
        orderErrors = new HashMap<>();
        for (ProductItem product : products) {
            Product foundProduct = productRepository.findById(product.productId()).orElse(null);
            if (foundProduct == null){
                this.addNotFoundError(product.productId());

            }else if(product.quantity() > foundProduct.getStock()){
                addNotStockError(product.productId(), foundProduct.getStock());
            }
        }
        return orderErrors;

    }


    @Override
    public void makeOrder(List<ProductItem> products) throws OrderErrorException {

        // Errors in order, if is empty, then is a valid order
        Map<String, String> orderErrors = orderValidation(products);

        if (orderErrors.isEmpty()) {
            commitOrder(products);
        }else {
            throw new OrderErrorException(orderErrors.toString());
        }

    }


}
