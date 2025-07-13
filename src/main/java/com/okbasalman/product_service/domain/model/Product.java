package com.okbasalman.product_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
private Long id;
private String name;
private double price;
private int stock;


}
