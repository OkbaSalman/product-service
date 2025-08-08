package com.okbasalman.product_service.domain.service;

import java.util.List;


import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.model.Product;
import com.okbasalman.product_service.domain.port.input.ProductUseCase;
import com.okbasalman.product_service.domain.port.output.ProductRepositoryPort;

import jakarta.transaction.Transactional;

public class ProductService implements ProductUseCase{

    private final ProductRepositoryPort productRepositoryPort;

    public ProductService(ProductRepositoryPort productRepositoryPort){
        this.productRepositoryPort = productRepositoryPort;
    }
    

    @Override
    @Transactional
    public Product getProductById(Long id) {
        return productRepositoryPort.findById(id);
    }

    @Override
    @Transactional
    public List<Product> getAllProducts() {
        return productRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public Product createProduct(ProductCreateDto dto){
        return productRepositoryPort.create(dto);
    }

    @Override
    @Transactional
    public Product updateProduct(Product product){
        return productRepositoryPort.update(product);
    }

    @Override
    @Transactional
    public DeleteProductResultDto deleteProduct(Long id){
        return productRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public Product decreaseStock(Long productVariantId, int quantity){
        return productRepositoryPort.decreaseStock(productVariantId, quantity);
    }
}
