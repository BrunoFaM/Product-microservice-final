package com.example.product_service.services.ServicesImplementation;

import com.example.product_service.dtos.NewProduct;
import com.example.product_service.dtos.ProductDTO;
import com.example.product_service.dtos.ProductItem;
import com.example.product_service.exceptions.OrderErrorException;
import com.example.product_service.exceptions.ProductNotFoundException;
import com.example.product_service.models.Product;
import com.example.product_service.repositories.ProductRepository;
import com.example.product_service.services.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Transactional
    private void commitOrder(List<ProductItem> products){
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

    private List<ProductItem> mergeEqualProducts(List<ProductItem> products){
        HashMap<Long , ProductItem> uniqueProducts = new HashMap<>();
        //create a hashMap, where the products with the same id are merged, in only one product with the stock of each one added
        for (ProductItem productItem : products) {
            if(!uniqueProducts.containsKey(productItem.productId())) {
                uniqueProducts.put(productItem.productId(), productItem);
            }else {
                ProductItem product = uniqueProducts.get(productItem.productId());
                ProductItem newProduct = new ProductItem(product.productId(), product.quantity() + productItem.quantity());
                uniqueProducts.replace(product.productId(), newProduct);
            }
        }
        return uniqueProducts.values().stream().toList();
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
    //
    private Map<String, String> orderValidation(List<ProductItem> products){

        products =this.mergeEqualProducts(products);

        return completeTheErrorMap(products);

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
