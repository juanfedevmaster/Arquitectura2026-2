package com.tienda.orders.dto;

import com.tienda.orders.model.Cart;
import com.tienda.orders.model.CartItem;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class CartResponse {
    private final UUID cartId;
    private final List<ItemDetail> items;
    private final BigDecimal total;

    public CartResponse(Cart cart) {
        this.cartId = cart.getId();
        this.items = cart.getItems().stream().map(ItemDetail::new).toList();
        this.total = cart.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Getter
    public static class ItemDetail {
        private final UUID itemId;
        private final UUID productId;
        private final Integer quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal subtotal;

        public ItemDetail(CartItem item) {
            this.itemId = item.getId();
            this.productId = item.getProductId();
            this.quantity = item.getQuantity();
            this.unitPrice = item.getUnitPrice();
            this.subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        }
    }
}
