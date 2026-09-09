package com.juanfedevmaster.salesmicroservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Respuesta con el detalle de una venta registrada")
public class SaleResponse {

    @Schema(description = "ID de la venta")
    private Long id;

    @Schema(description = "Fecha y hora de la venta")
    private LocalDateTime saleDate;

    @Schema(description = "Total de la venta")
    private BigDecimal totalAmount;

    @Schema(description = "Ítems de la venta")
    private List<SaleItemResponse> items;
}
