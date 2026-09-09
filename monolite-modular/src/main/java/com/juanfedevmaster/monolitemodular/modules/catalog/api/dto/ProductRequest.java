package com.juanfedevmaster.monolitemodular.modules.catalog.api.dto;

import com.juanfedevmaster.monolitemodular.modules.catalog.domain.UnitMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank @Size(max = 50) String sku,
    @NotBlank @Size(max = 150) String name,
    @NotNull Long categoryId,
    @NotNull UnitMeasure unitMeasure,
    @NotNull @Positive BigDecimal purchasePrice,
    @NotNull @Positive BigDecimal salePrice,
    @NotNull @PositiveOrZero Integer minStock
) {}
