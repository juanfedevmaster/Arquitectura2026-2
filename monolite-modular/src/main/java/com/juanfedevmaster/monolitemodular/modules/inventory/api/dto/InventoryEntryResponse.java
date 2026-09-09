package com.juanfedevmaster.monolitemodular.modules.inventory.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryEntryResponse(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal cost,
    LocalDateTime entryDate,
    Long supplierId,
    String supplierName
) {}
