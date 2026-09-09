package com.juanfedevmaster.monolitemodular.modules.reports.api.dto;

public record LowStockReport(
    Long productId,
    String productSku,
    String productName,
    Integer currentStock,
    Integer minStock
) {}
