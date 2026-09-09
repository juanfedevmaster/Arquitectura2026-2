package com.juanfedevmaster.inventorymicroservice.service;

import com.juanfedevmaster.inventorymicroservice.domain.dto.ProductStockResponse;
import com.juanfedevmaster.inventorymicroservice.domain.dto.SaleCreatedEvent;
import com.juanfedevmaster.inventorymicroservice.domain.dto.StockMovementResponse;

import java.util.List;

public interface StockService {

    void processStockReduction(SaleCreatedEvent event);

    ProductStockResponse getStockByProductId(String productId);

    List<ProductStockResponse> getAllStock();

    List<StockMovementResponse> getMovementsByProductId(String productId);

    List<StockMovementResponse> getMovementsBySaleId(String saleId);
}
