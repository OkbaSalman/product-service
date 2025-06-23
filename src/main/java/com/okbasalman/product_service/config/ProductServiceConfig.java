package com.okbasalman.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.okbasalman.product_service.domain.port.output.ProductRepositoryPort;
import com.okbasalman.product_service.domain.service.ProductService;

@Configuration
public class ProductServiceConfig {

    @Bean
    public ProductService productService(ProductRepositoryPort repositoryPort){
        return new ProductService(repositoryPort);
    }
}
