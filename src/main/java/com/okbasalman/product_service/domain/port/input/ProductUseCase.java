package com.okbasalman.product_service.domain.port.input;

import java.util.List;

import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.model.Product;

public interface ProductUseCase {
    Product createProduct(ProductCreateDto dto);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product updateProduct(Product product);
    DeleteProductResultDto deleteProduct(Long id);
    Product decreaseStock(Long productVariantId, int quantity);
}
