package com.example.product_service.services.ServicesImplementation;

import com.example.product_service.dtos.*;
import com.example.product_service.exceptions.OrderErrorException;
import com.example.product_service.exceptions.ProductNotFoundException;
import com.example.product_service.models.Product;
import com.example.product_service.repositories.ProductRepository;
import com.example.product_service.services.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

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

    @Transactional
    public void commitOrder(List<ProductItem> products){
        for(ProductItem productItem : products){
            Product product = productRepository.findById(productItem.productId()).orElse(null);
            Integer newStock = product.getStock() - productItem.quantity();
            product.setStock(newStock);
            productRepository.save(product);


        }
    }

    private void addNotFoundError(Long productId){
        orderErrors.put( "Product " + productId , "not found");
    }
    private void addNotStockError(Long productId, Integer actualStock){
        orderErrors.put("Product " + productId, "only "+  actualStock + " units left");
    }


    private Map<String, String> completeTheErrorMap(List<ProductItem> products){
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
    private Map<String, String> completeTheErrorMapForStockReduce(List<ProductItem> products){
        HashMap<String, String> orderErrorsMap = new HashMap<>();
        for (ProductItem product : products) {
            Product foundProduct = productRepository.findById(product.productId()).orElse(null);
            if (foundProduct == null){
                orderErrorsMap.put( "Product " + product.productId() , "not found");

            }else if(product.quantity() > foundProduct.getStock()){
                orderErrorsMap.put("Product " + product.productId(), "only "+  product.quantity() + " units left");
            }
        }
        return orderErrorsMap;
    }


    @Override
    public void makeOrder(List<ProductItem> products) throws OrderErrorException {

        // Errors in order, if is empty, then is a valid order
        Map<String, String> orderErrors = completeTheErrorMap(products);
        System.out.println(orderErrors.toString());

        if (orderErrors.isEmpty()) {
            commitOrder(products);
        }else {
            throw new OrderErrorException(orderErrors.toString());
        }

    }

    private String errorFormater(String error){
        if(error.equals("{}")){
            return "";
        }else {
            return  error.replace("{", "").replace("}", "").replace("=", ": ").replace(", ","\n");
        }

    }

    @Override
    public void validateProductList(List<ProductItem> products) {
        Map<String, String> orderErrors = completeTheErrorMap(products);
        if (!orderErrors.isEmpty()){
            throw new OrderErrorException(orderErrors.toString());
        }
    }
    //this is the asynchronous call that reduces stock
    @Override
    public void validateStockAndReduce(ReduceStockRequest reduceStockRequest) {
        Map<String, String> orderErrorsMap = completeTheErrorMap(reduceStockRequest.products());
        //if is valid right now
        //send the success or the failure to the server
        System.out.println("VALIIIIIDDDDDAAATION");
        if (orderErrorsMap.isEmpty()){
            System.out.println("TRUE");
            commitOrder(reduceStockRequest.products());
            amqpTemplate.convertAndSend("responseReduceStockExchange", "routing.key2", new ResponseReduceStock(reduceStockRequest.orderId(), true));
        }else{
            System.out.println("FALSE");
            amqpTemplate.convertAndSend("responseReduceStockExchange", "routing.key2", new ResponseReduceStock(reduceStockRequest.orderId(), false));
        }

    }

}
