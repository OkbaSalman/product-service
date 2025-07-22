package com.okbasalman.product_service.domain.dto;

import com.okbasalman.product_service.domain.model.Season;
import com.okbasalman.product_service.domain.model.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductCreateDto {
    private String name;
    private double price;
    private int stock;
    private String[] imagesUrls;
    private String color;
    private Size size;
    private Season season;
}
