package com.example.product_service.services.ServicesImplementation;

import com.example.product_service.dtos.*;
import com.example.product_service.exceptions.OrderErrorException;
import com.example.product_service.exceptions.ProductNotFoundException;
import com.example.product_service.models.Product;
import com.example.product_service.repositories.ProductRepository;
import com.example.product_service.services.MessageSenderService;
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
    private MessageSenderService messageSenderService;

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
    @Transactional
    public ProductDTO createProduct(NewProduct newProduct) {
        Product product = new Product(newProduct.name(), newProduct.description(), newProduct.price(), newProduct.stock());

        product = productRepository.save(product);
        return new ProductDTO(product);
    }


    @Override
    @Transactional
    public void updateProductStock(Long id, Integer stock) throws ProductNotFoundException {
        Product product = getProductById(id);
        product.setStock(stock);
        productRepository.save(product);

    }
    //Exception handler is missing
    @Override
    @Transactional
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
            if(newStock < 10){
                System.out.println("Inside the sendLowStock for");
                System.out.println(product.getStock());
                messageSenderService.sendLowStockMessage(new ProductLowStockDTO(product.getId(), product.getName(), product.getStock()));
            }


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
            if (foundProduct != null) {
                if (product.quantity() > foundProduct.getStock()) {
                    orderErrorsMap.put("Product " + product.productId(), "only " + product.quantity() + " units left");
                }
            }else {
                orderErrorsMap.put("Product " + product.productId(), "not found");
            }
        }
        return orderErrorsMap;
    }


    @Override
    public void makeOrder(List<ProductItem> products) throws OrderErrorException {

        // Errors in order, if is empty, then is a valid order
        Map<String, String> orderErrors = completeTheErrorMap(products);

        if (orderErrors.isEmpty()) {
            commitOrder(products);
        }else {
            throw new OrderErrorException(orderErrors.toString());
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
        Map<String, String> orderErrorsMap = completeTheErrorMapForStockReduce(reduceStockRequest.products());
        //if is valid right now
        //send the success or the failure to the server
        if (orderErrorsMap.isEmpty()){
            commitOrder(reduceStockRequest.products());
            messageSenderService.sendResponseReduceStockMessage(new ResponseReduceStock(reduceStockRequest.orderId(), true));
        }else{
            System.out.println("All goood");
            messageSenderService.sendResponseReduceStockMessage(new ResponseReduceStock(reduceStockRequest.orderId(), false));
        }

    }
    @Override
    public List<ProductDetails> giveTheDetailsOfAll(List<ProductItem> products) throws ProductNotFoundException {
        List<ProductDetails> detailsList = new ArrayList<>();

        for(ProductItem productItem : products){
            Product product = this.getProductById(productItem.productId());
            detailsList.add(new ProductDetails(product.getId(),product.getName(), productItem.quantity(), product.getPrice(), productItem.quantity() * product.getPrice()));
        }

        return detailsList;
    }

}
