package com.juanfedevmaster.billingmicroservices.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrato del evento publicado por sales-microservices en sales.topic con routing key sale.created.
 * Debe mantenerse sincronizado con el productor.
 */
@Data
public class SaleCreatedEvent {

    private String eventId;
    private String eventType;
    private String occurredAt;
    private String saleId;
    private String customerId;
    private List<SaleItemEvent> items;
    private BigDecimal totalAmount;

    @Data
    public static class SaleItemEvent {
        private String productId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
