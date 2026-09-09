package com.juanfedevmaster.monolitemodular.modules.inventory.api.dto;

import com.juanfedevmaster.monolitemodular.modules.inventory.domain.ExitReason;

import java.time.LocalDateTime;

public record InventoryExitResponse(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    ExitReason reason,
    String notes,
    LocalDateTime exitDate
) {}
