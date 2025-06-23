package com.okbasalman.product_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteProductResultDto {
    private boolean success;
    private String message;
}
