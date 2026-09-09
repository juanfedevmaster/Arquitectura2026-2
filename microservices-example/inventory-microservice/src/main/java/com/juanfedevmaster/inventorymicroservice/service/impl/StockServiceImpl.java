package com.juanfedevmaster.inventorymicroservice.service.impl;

import com.juanfedevmaster.inventorymicroservice.domain.dto.ProductStockResponse;
import com.juanfedevmaster.inventorymicroservice.domain.dto.SaleCreatedEvent;
import com.juanfedevmaster.inventorymicroservice.domain.dto.StockMovementResponse;
import com.juanfedevmaster.inventorymicroservice.domain.entity.ProductStock;
import com.juanfedevmaster.inventorymicroservice.domain.entity.StockMovement;
import com.juanfedevmaster.inventorymicroservice.repository.ProductStockRepository;
import com.juanfedevmaster.inventorymicroservice.repository.StockMovementRepository;
import com.juanfedevmaster.inventorymicroservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductStockRepository productStockRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional
    public void processStockReduction(SaleCreatedEvent event) {
        for (SaleCreatedEvent.SaleItemEvent item : event.getItems()) {
            String productId = item.getProductId();

            // Idempotencia: (eventId, productId) ya procesado → ignorar
            if (stockMovementRepository.existsByEventIdAndProductId(event.getEventId(), productId)) {
                log.warn("Movimiento duplicado ignorado: eventId={}, productId={}", event.getEventId(), productId);
                continue;
            }

            ProductStock stock = productStockRepository.findByProductId(productId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "Producto no encontrado en inventario: " + productId));

            if (stock.getCurrentStock() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente en inventario para producto: " + productId);
            }

            stock.setCurrentStock(stock.getCurrentStock() - item.getQuantity());
            productStockRepository.save(stock);

            StockMovement movement = StockMovement.builder()
                    .productId(productId)
                    .saleId(event.getSaleId())
                    .eventId(event.getEventId())
                    .quantity(item.getQuantity())
                    .movedAt(LocalDateTime.now())
                    .movementType("SALE_REDUCTION")
                    .build();

            stockMovementRepository.save(movement);
            log.info("Stock descontado: productId={}, cantidad={}, venta={}",
                    productId, item.getQuantity(), event.getSaleId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStockResponse getStockByProductId(String productId) {
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Producto no encontrado en inventario: " + productId));
        return toStockResponse(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStockResponse> getAllStock() {
        return productStockRepository.findAll().stream()
                .map(this::toStockResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> getMovementsByProductId(String productId) {
        return stockMovementRepository.findByProductIdOrderByMovedAtDesc(productId).stream()
                .map(this::toMovementResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> getMovementsBySaleId(String saleId) {
        return stockMovementRepository.findBySaleId(saleId).stream()
                .map(this::toMovementResponse)
                .toList();
    }

    private ProductStockResponse toStockResponse(ProductStock stock) {
        return ProductStockResponse.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .productName(stock.getProductName())
                .currentStock(stock.getCurrentStock())
                .build();
    }

    private StockMovementResponse toMovementResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .productId(movement.getProductId())
                .saleId(movement.getSaleId())
                .quantity(movement.getQuantity())
                .movedAt(movement.getMovedAt())
                .movementType(movement.getMovementType())
                .build();
    }
}
