package com.okbasalman.product_service.domain.dto;

import java.util.List;

import com.okbasalman.product_service.domain.model.Season;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {
    private String name;
    private String description;
    private Season season;
    private List<ProductVariantCreateDto> variants;
}