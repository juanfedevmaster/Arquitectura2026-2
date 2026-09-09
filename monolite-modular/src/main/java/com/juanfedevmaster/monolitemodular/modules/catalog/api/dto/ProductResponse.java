package com.juanfedevmaster.monolitemodular.modules.catalog.api.dto;

import com.juanfedevmaster.monolitemodular.modules.catalog.domain.UnitMeasure;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    Long categoryId,
    String categoryName,
    UnitMeasure unitMeasure,
    BigDecimal purchasePrice,
    BigDecimal salePrice,
    Integer minStock,
    Integer stockQuantity,
    boolean active,
    boolean belowMinStock
) {}
