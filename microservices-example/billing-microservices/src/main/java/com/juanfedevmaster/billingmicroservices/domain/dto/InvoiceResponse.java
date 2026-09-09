package com.juanfedevmaster.billingmicroservices.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Factura generada a partir de una venta")
public class InvoiceResponse {

    @Schema(description = "ID interno de la factura")
    private Long id;

    @Schema(description = "Número de factura", example = "INV-000001")
    private String invoiceNumber;

    @Schema(description = "ID de la venta origen en sales-microservices", example = "S-00123")
    private String saleId;

    @Schema(description = "ID del cliente", example = "C-4521")
    private String customerId;

    @Schema(description = "Total de la factura")
    private BigDecimal totalAmount;

    @Schema(description = "Fecha de emisión")
    private LocalDateTime issuedAt;

    @Schema(description = "Estado de la factura", example = "ISSUED")
    private String status;

    @Schema(description = "Líneas de detalle")
    private List<InvoiceItemResponse> items;
}
