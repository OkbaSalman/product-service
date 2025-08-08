package com.okbasalman.product_service.adapter.output;

import com.okbasalman.product_service.adapter.output.entity.ProductEntity;
import com.okbasalman.product_service.adapter.output.entity.ProductImageEntity;
import com.okbasalman.product_service.adapter.output.entity.ProductVariantEntity;
import com.okbasalman.product_service.adapter.output.repository.ProductRepository;
import com.okbasalman.product_service.adapter.output.repository.ProductVariantRepository;
import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.model.Product;
import com.okbasalman.product_service.domain.model.ProductImage;
import com.okbasalman.product_service.domain.model.ProductVariant;
import com.okbasalman.product_service.domain.port.output.ProductRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JpaProductRepository implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public JpaProductRepository(ProductRepository productRepository, ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public Product findById(Long id) {
        ProductEntity entity = productRepository.findByIdWithVariantsAndImages(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return mapToProduct(entity);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAllWithVariants().stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
    }

    @Override
    public Product create(ProductCreateDto dto) {
        ProductEntity productEntity = mapToProductEntity(dto);
        ProductEntity savedEntity = productRepository.save(productEntity);
        return mapToProduct(savedEntity);
    }

    @Override
    public DeleteProductResultDto deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            return new DeleteProductResultDto(false, "Product not found.");
        }
        productRepository.deleteById(id);
        return new DeleteProductResultDto(true, "Product deleted successfully.");
    }

    @Override
    public Product update(Product product) {
        ProductEntity entity = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + product.getId()));

        // Update core product fields
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setSeason(product.getSeason());

        // We will assume a simple update strategy: replace all variants.
        // This is a powerful feature of using cascade.
        entity.getVariants().clear();
        entity.getVariants().addAll(mapProductVariantsToEntities(product.getVariants(), entity));

        ProductEntity updatedEntity = productRepository.save(entity);
        return mapToProduct(updatedEntity);
    }

    @Override
    public Product decreaseStock(Long productVariantId, int quantity) {
        ProductVariantEntity variantEntity = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found with ID: " + productVariantId));

        if (variantEntity.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product variant ID: " + productVariantId);
        }

        variantEntity.setStock(variantEntity.getStock() - quantity);
        ProductVariantEntity updatedVariant = productVariantRepository.save(variantEntity);
        
        return mapToProduct(updatedVariant.getProduct());
    }

    //region Mappers
    private Product mapToProduct(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setSeason(entity.getSeason());
        product.setVariants(mapToProductVariants(entity.getVariants()));
        return product;
    }

    private List<ProductVariant> mapToProductVariants(List<ProductVariantEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToProductVariant)
                .collect(Collectors.toList());
    }

    private ProductVariant mapToProductVariant(ProductVariantEntity entity) {
        ProductVariant variant = new ProductVariant();
        variant.setId(entity.getId());
        variant.setPrice(entity.getPrice());
        variant.setStock(entity.getStock());
        variant.setColor(entity.getColor());
        variant.setSize(entity.getSize());
        variant.setImages(mapToProductImages(entity.getImages()));
        return variant;
    }

    private List<ProductImage> mapToProductImages(List<ProductImageEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToProductImage)
                .collect(Collectors.toList());
    }

    private ProductImage mapToProductImage(ProductImageEntity entity) {
        ProductImage image = new ProductImage();
        image.setId(entity.getId());
        image.setBase64Data(entity.getBase64Data());
        return image;
    }

    private ProductEntity mapToProductEntity(ProductCreateDto dto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(dto.getName());
        productEntity.setDescription(dto.getDescription());
        productEntity.setSeason(dto.getSeason());
        
        List<ProductVariantEntity> variantEntities = dto.getVariants().stream()
                .map(variantDto -> {
                    ProductVariantEntity variantEntity = new ProductVariantEntity();
                    variantEntity.setPrice(variantDto.getPrice());
                    variantEntity.setStock(variantDto.getStock());
                    variantEntity.setColor(variantDto.getColor());
                    variantEntity.setSize(variantDto.getSize());
                    
                    List<ProductImageEntity> imageEntities = variantDto.getBase64Images().stream()
                        .map(base64 -> {
                            ProductImageEntity imageEntity = new ProductImageEntity();
                            imageEntity.setBase64Data(base64);
                            imageEntity.setProductVariant(variantEntity);
                            return imageEntity;
                        })
                        .collect(Collectors.toList());
                    
                    variantEntity.setImages(imageEntities);
                    variantEntity.setProduct(productEntity);
                    return variantEntity;
                })
                .collect(Collectors.toList());
        
        productEntity.setVariants(variantEntities);
        return productEntity;
    }
    
    private List<ProductVariantEntity> mapProductVariantsToEntities(List<ProductVariant> variants, ProductEntity productEntity) {
        if (variants == null) {
            return new ArrayList<>();
        }
        return variants.stream()
                .map(variant -> {
                    ProductVariantEntity entity = new ProductVariantEntity();
                    entity.setId(variant.getId());
                    entity.setPrice(variant.getPrice());
                    entity.setStock(variant.getStock());
                    entity.setColor(variant.getColor());
                    entity.setSize(variant.getSize());
                    entity.setProduct(productEntity);
                    entity.setImages(mapProductImagesToEntities(variant.getImages(), entity));
                    return entity;
                })
                .collect(Collectors.toList());
    }

    private List<ProductImageEntity> mapProductImagesToEntities(List<ProductImage> images, ProductVariantEntity variantEntity) {
        if (images == null) {
            return new ArrayList<>();
        }
        return images.stream()
                .map(image -> {
                    ProductImageEntity entity = new ProductImageEntity();
                    entity.setId(image.getId());
                    entity.setBase64Data(image.getBase64Data());
                    entity.setProductVariant(variantEntity);
                    return entity;
                })
                .collect(Collectors.toList());
    }
    //endregion

   
}