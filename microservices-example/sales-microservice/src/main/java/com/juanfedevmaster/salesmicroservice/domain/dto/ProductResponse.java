package com.juanfedevmaster.salesmicroservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Producto comestible disponible")
public class ProductResponse {

    @Schema(description = "ID del producto")
    private Long id;

    @Schema(description = "Nombre del producto")
    private String name;

    @Schema(description = "Descripción")
    private String description;

    @Schema(description = "Precio unitario")
    private BigDecimal price;

    @Schema(description = "Categoría", example = "COMESTIBLE")
    private String category;

    @Schema(description = "Stock disponible")
    private Integer stock;
}
