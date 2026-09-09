package com.juanfedevmaster.monolitemodular.modules.sales.api.dto;

import com.juanfedevmaster.monolitemodular.modules.sales.domain.PaymentMethod;
import com.juanfedevmaster.monolitemodular.modules.sales.domain.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
    Long id,
    LocalDateTime saleDate,
    PaymentMethod paymentMethod,
    BigDecimal total,
    SaleStatus status,
    List<SaleItemResponse> items
) {}
