package com.juanfedevmaster.salesmicroservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Solicitud para registrar una venta")
public class CreateSaleRequest {

    @Schema(description = "Lista de productos e items a vender", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SaleItemRequest> items;
}
