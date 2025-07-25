package com.okbasalman.product_service.domain.port.output;

import java.util.List;

import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.model.Product;

public interface ProductRepositoryPort {
    Product create(ProductCreateDto dto);
    Product findById(Long id);
    List<Product> findAll();
    Product update(Product product);
    DeleteProductResultDto deleteById(Long id);
    Product decreaseStock(Long id, int quantity);
}
