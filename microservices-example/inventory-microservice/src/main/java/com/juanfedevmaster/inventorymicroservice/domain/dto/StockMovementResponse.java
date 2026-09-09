package com.juanfedevmaster.inventorymicroservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Movimiento de stock registrado a partir de un evento de venta")
public class StockMovementResponse {

    @Schema(description = "ID del movimiento")
    private Long id;

    @Schema(description = "ID del producto")
    private String productId;

    @Schema(description = "ID de la venta origen", example = "S-00123")
    private String saleId;

    @Schema(description = "Cantidad descontada")
    private Integer quantity;

    @Schema(description = "Fecha del movimiento")
    private LocalDateTime movedAt;

    @Schema(description = "Tipo de movimiento", example = "SALE_REDUCTION")
    private String movementType;
}
