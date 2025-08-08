package com.okbasalman.product_service.adapter.output.repository;

import com.okbasalman.product_service.adapter.output.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.variants pv WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithVariantsAndImages(Long id);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.variants")
    List<ProductEntity> findAllWithVariants();
}