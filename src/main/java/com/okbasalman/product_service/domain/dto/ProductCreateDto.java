package com.okbasalman.product_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductCreateDto {
    private String name;
    private double price;
    private int stock;
    private String[] imagesUrls;
}
