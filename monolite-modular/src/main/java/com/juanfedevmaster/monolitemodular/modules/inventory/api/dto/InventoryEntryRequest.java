package com.juanfedevmaster.monolitemodular.modules.inventory.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryEntryRequest(
    @NotNull Long productId,
    @NotNull @Positive Integer quantity,
    @NotNull @Positive BigDecimal cost,
    LocalDateTime entryDate,
    Long supplierId
) {}
