package com.okbasalman.product_service.adapter.output.entity;

import java.time.LocalDateTime;

import com.okbasalman.product_service.domain.model.Season;
import com.okbasalman.product_service.domain.model.Size;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private int stock;
    private String[] imagesUrls;
    private String color;
    private Size size;
    private Season season;
    

}
