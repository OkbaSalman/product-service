package com.okbasalman.product_service.adapter.output.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.okbasalman.product_service.adapter.output.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer>{

}
