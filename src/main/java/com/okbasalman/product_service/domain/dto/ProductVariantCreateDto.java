package com.okbasalman.product_service.domain.dto;

import java.util.List;

import com.okbasalman.product_service.domain.model.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantCreateDto {
    private double price;
    private int stock;
    private String color;
    private Size size;
    private List<String> base64Images;
}