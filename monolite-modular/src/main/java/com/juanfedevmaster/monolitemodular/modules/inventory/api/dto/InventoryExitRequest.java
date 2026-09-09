package com.juanfedevmaster.monolitemodular.modules.inventory.api.dto;

import com.juanfedevmaster.monolitemodular.modules.inventory.domain.ExitReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventoryExitRequest(
    @NotNull Long productId,
    @NotNull @Positive Integer quantity,
    @NotNull ExitReason reason,
    String notes
) {}
