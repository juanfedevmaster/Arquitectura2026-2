package com.juanfedevmaster.monolitemodular.modules.sales.api.dto;

import java.math.BigDecimal;

public record SaleItemResponse(
    Long productId,
    String productName,
    String productSku,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {}
