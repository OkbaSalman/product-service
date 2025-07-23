package com.okbasalman.product_service.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private double price;
    private int stock;
    private String[] imagesUrls;
    private String color;
    private Size size;
    private Season season;
    private String description;

}
