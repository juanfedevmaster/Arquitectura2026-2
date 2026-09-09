package com.juanfedevmaster.billingmicroservices.controller;

import com.juanfedevmaster.billingmicroservices.domain.dto.InvoiceResponse;
import com.juanfedevmaster.billingmicroservices.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Facturación", description = "Consulta de facturas generadas desde eventos de venta")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Operation(summary = "Listar todas las facturas")
    @ApiResponse(responseCode = "200", description = "Lista de facturas")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura por ID")
    @ApiResponse(responseCode = "200", description = "Factura encontrada")
    @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/by-sale/{saleId}")
    @Operation(summary = "Obtener factura por ID de venta",
               description = "Permite correlacionar la factura con la venta original en sales-microservices")
    @ApiResponse(responseCode = "200", description = "Factura encontrada")
    @ApiResponse(responseCode = "404", description = "No hay factura para esa venta")
    public ResponseEntity<InvoiceResponse> getInvoiceBySaleId(@PathVariable String saleId) {
        return ResponseEntity.ok(invoiceService.getInvoiceBySaleId(saleId));
    }
}
