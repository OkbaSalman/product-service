package com.okbasalman.product_service.adapter.output.repository;

import com.okbasalman.product_service.adapter.output.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, Long> {
}