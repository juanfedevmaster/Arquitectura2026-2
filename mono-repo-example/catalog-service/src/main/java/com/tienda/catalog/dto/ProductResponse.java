package com.tienda.catalog.dto;

import com.tienda.catalog.model.Product;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ProductResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final Integer stock;
    private final String imageUrl;
    private final String categoryName;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.imageUrl = product.getImageUrl();
        this.categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
    }
}
