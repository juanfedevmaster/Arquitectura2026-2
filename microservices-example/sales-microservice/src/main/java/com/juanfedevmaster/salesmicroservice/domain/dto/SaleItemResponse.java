package com.juanfedevmaster.salesmicroservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Detalle de un ítem dentro de una venta")
public class SaleItemResponse {

    @Schema(description = "ID del ítem")
    private Long id;

    @Schema(description = "ID del producto")
    private Long productId;

    @Schema(description = "Nombre del producto")
    private String productName;

    @Schema(description = "Cantidad vendida")
    private Integer quantity;

    @Schema(description = "Precio unitario al momento de la venta")
    private BigDecimal unitPrice;

    @Schema(description = "Subtotal del ítem")
    private BigDecimal subtotal;
}
