package com.okbasalman.product_service.adapter.output;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.okbasalman.product_service.adapter.output.entity.ProductEntity;
import com.okbasalman.product_service.adapter.output.repository.ProductRepository;
import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.model.Product;
import com.okbasalman.product_service.domain.port.output.ProductRepositoryPort;

@Repository
public class JpaProductRepository implements ProductRepositoryPort{

    @Autowired
    private final ProductRepository repository;

    public  JpaProductRepository(ProductRepository repository){
        this.repository = repository;
    }

    @Override
    public Product findById(Long id) {
       ProductEntity entity =  repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

       return new Product(entity.getId(), entity.getName(), entity.getPrice(), entity.getStock(), entity.getImagesUrls());
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(e -> new Product(e.getId(), e.getName(), e.getPrice(), e.getStock(), e.getImagesUrls())).collect(Collectors.toList());
    }

    @Override
    public Product create(ProductCreateDto dto) {
        ProductEntity entity = new ProductEntity();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setImagesUrls(dto.getImagesUrls());

        ProductEntity savedEntity = repository.save(entity);

        return new Product(savedEntity.getId(), savedEntity.getName(), savedEntity.getPrice(), savedEntity.getStock(), savedEntity.getImagesUrls());
    }

    @Override
    public DeleteProductResultDto deleteById(Long id){
        if (!repository.existsById(id)) {
        return new DeleteProductResultDto(false, "Product not found.");
        }
        repository.deleteById(id);
        return new DeleteProductResultDto(true, "Product deleted successfully.");
    }

    @Override
    public Product update(Product product){
        ProductEntity entity =  repository.findById(product.getId()).orElseThrow(() -> new RuntimeException("Product not found with ID: " + product.getId()));

        entity.setName(product.getName());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setImagesUrls(product.getImagesUrls());

        ProductEntity updatedEntity = repository.save(entity);
        return new Product(updatedEntity.getId(), updatedEntity.getName(), updatedEntity.getPrice(), updatedEntity.getStock(), updatedEntity.getImagesUrls());
    }

    @Override
    public Product decreaseStock(Long productId, int quantity) {
        ProductEntity entity = repository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (entity.getStock() < quantity) {
          throw new RuntimeException("Insufficient stock for product ID: " + productId);
     }

        entity.setStock(entity.getStock() - quantity);
     ProductEntity updated = repository.save(entity);

        return new Product(updated.getId(), updated.getName(), updated.getPrice(), updated.getStock(), updated.getImagesUrls());
    }

    
}
