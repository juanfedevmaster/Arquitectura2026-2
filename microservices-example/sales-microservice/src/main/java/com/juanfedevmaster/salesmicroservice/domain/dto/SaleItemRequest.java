package com.juanfedevmaster.salesmicroservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ítem dentro de una solicitud de venta")
public class SaleItemRequest {

    @Schema(description = "ID del producto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @Schema(description = "Cantidad a vender", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
