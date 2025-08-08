package com.okbasalman.product_service.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class ProductVariant {
    private Long id;
    private double price;
    private int stock;
    private String color;
    private Size size;
    private List<ProductImage> images;
}