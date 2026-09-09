package com.juanfedevmaster.monolitemodular.modules.sales.api.dto;

import com.juanfedevmaster.monolitemodular.modules.sales.domain.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleRequest(
    @NotNull PaymentMethod paymentMethod,
    @NotNull @NotEmpty List<SaleItemRequest> items
) {}
