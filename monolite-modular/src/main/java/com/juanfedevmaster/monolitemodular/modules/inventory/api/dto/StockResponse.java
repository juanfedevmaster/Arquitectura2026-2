package com.juanfedevmaster.monolitemodular.modules.inventory.api.dto;

public record StockResponse(
    Long productId,
    String productSku,
    String productName,
    Integer stockQuantity,
    Integer minStock,
    boolean belowMinStock
) {}
