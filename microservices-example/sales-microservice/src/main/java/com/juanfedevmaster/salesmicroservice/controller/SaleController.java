package com.juanfedevmaster.salesmicroservice.controller;

import com.juanfedevmaster.salesmicroservice.domain.dto.CreateSaleRequest;
import com.juanfedevmaster.salesmicroservice.domain.dto.ProductResponse;
import com.juanfedevmaster.salesmicroservice.domain.dto.SaleResponse;
import com.juanfedevmaster.salesmicroservice.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Gestión de ventas de productos comestibles")
public class SaleController {

    private final SaleService saleService;

    @GetMapping("/products")
    @Operation(summary = "Listar productos disponibles",
               description = "Retorna todos los productos comestibles con stock > 0")
    @ApiResponse(responseCode = "200", description = "Lista de productos")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        return ResponseEntity.ok(saleService.getAvailableProducts());
    }

    @GetMapping("/sales")
    @Operation(summary = "Listar todas las ventas")
    @ApiResponse(responseCode = "200", description = "Lista de ventas registradas")
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/sales/{id}")
    @Operation(summary = "Obtener venta por ID")
    @ApiResponse(responseCode = "200", description = "Venta encontrada")
    @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @PostMapping("/sales")
    @Operation(summary = "Registrar una venta",
               description = "Crea una venta, descuenta stock y calcula el total automáticamente")
    @ApiResponse(responseCode = "201", description = "Venta registrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Stock insuficiente o producto no existe")
    public ResponseEntity<SaleResponse> createSale(@RequestBody CreateSaleRequest request) {
        SaleResponse response = saleService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
