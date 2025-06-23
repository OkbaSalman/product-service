package com.okbasalman.product_service.domain.service;

import java.util.List;

import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.model.Product;
import com.okbasalman.product_service.domain.port.input.ProductUseCase;
import com.okbasalman.product_service.domain.port.output.ProductRepositoryPort;

public class ProductService implements ProductUseCase{

    private final ProductRepositoryPort productRepositoryPort;

    public ProductService(ProductRepositoryPort productRepositoryPort){
        this.productRepositoryPort = productRepositoryPort;
    }
    

    @Override
    public Product getProductById(Integer id) {
        return productRepositoryPort.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepositoryPort.findAll();
    }

    @Override
    public Product createProduct(ProductCreateDto dto){
        return productRepositoryPort.create(dto);
    }

    @Override
    public Product updateProduct(Product product){
        return productRepositoryPort.update(product);
    }

    @Override
    public DeleteProductResultDto deleteProduct(Integer id){
        return productRepositoryPort.deleteById(id);
    }
}
