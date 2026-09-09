package com.juanfedevmaster.inventorymicroservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Stock actual de un producto")
public class ProductStockResponse {

    @Schema(description = "ID interno del registro")
    private Long id;

    @Schema(description = "ID del producto en sales-microservices", example = "1")
    private String productId;

    @Schema(description = "Nombre del producto")
    private String productName;

    @Schema(description = "Unidades disponibles en inventario")
    private Integer currentStock;
}
