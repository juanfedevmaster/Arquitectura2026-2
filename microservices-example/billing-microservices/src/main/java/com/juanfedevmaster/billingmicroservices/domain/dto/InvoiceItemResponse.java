package com.juanfedevmaster.billingmicroservices.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Línea de detalle de una factura")
public class InvoiceItemResponse {

    @Schema(description = "ID del ítem")
    private Long id;

    @Schema(description = "ID del producto", example = "P-9001")
    private String productId;

    @Schema(description = "Cantidad facturada")
    private Integer quantity;

    @Schema(description = "Precio unitario")
    private BigDecimal unitPrice;

    @Schema(description = "Subtotal del ítem")
    private BigDecimal subtotal;
}
