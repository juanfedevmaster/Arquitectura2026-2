package com.tienda.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class CartItemRequest {
    @NotNull
    private UUID productId;
    @NotNull @Min(1)
    private Integer quantity;
    @NotNull
    private BigDecimal unitPrice;
}
