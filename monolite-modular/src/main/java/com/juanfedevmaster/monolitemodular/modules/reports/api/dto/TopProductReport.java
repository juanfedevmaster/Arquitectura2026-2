package com.juanfedevmaster.monolitemodular.modules.reports.api.dto;

import java.math.BigDecimal;

public record TopProductReport(
    Long productId,
    String productName,
    Long totalQuantitySold,
    BigDecimal totalRevenue
) {}
