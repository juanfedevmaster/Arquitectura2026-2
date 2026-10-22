package com.tienda.orders.dto;

import com.tienda.orders.model.Order;
import com.tienda.orders.model.OrderItem;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderResponse {
    private final UUID orderId;
    private final String status;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private final List<ItemDetail> items;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.status = order.getStatus().name();
        this.total = order.getTotal();
        this.createdAt = order.getCreatedAt();
        this.items = order.getItems().stream().map(ItemDetail::new).toList();
    }

    @Getter
    public static class ItemDetail {
        private final UUID productId;
        private final Integer quantity;
        private final BigDecimal unitPrice;

        public ItemDetail(OrderItem item) {
            this.productId = item.getProductId();
            this.quantity = item.getQuantity();
            this.unitPrice = item.getUnitPrice();
        }
    }
}
