package com.example.product_service;

import com.example.product_service.models.Product;
import com.example.product_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

	@Autowired
	private ProductRepository productRepository;

	@Bean
	CommandLineRunner initData(){
		return args -> {
			Product product = new Product("Case", "pc case", 500D, 45);
			Product product1 = new Product("Graphics card", "for pc", 5000D, 45);
			Product product2 = new Product("Monitor", "pc case", 500D, 45);
			Product product3 = new Product("Mouse", "pc case", 500D, 45);
			Product product4 = new Product("Car", "sportive", 9999D, 45);

			productRepository.saveAll(List.of(product, product2, product3, product4, product1));
		};
	}

}
