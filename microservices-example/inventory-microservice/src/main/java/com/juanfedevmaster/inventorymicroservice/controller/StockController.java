package com.juanfedevmaster.inventorymicroservice.controller;

import com.juanfedevmaster.inventorymicroservice.domain.dto.ProductStockResponse;
import com.juanfedevmaster.inventorymicroservice.domain.dto.StockMovementResponse;
import com.juanfedevmaster.inventorymicroservice.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Consulta de stock y movimientos por evento de venta")
public class StockController {

    private final StockService stockService;

    @GetMapping
    @Operation(summary = "Listar stock de todos los productos")
    @ApiResponse(responseCode = "200", description = "Stock actual por producto")
    public ResponseEntity<List<ProductStockResponse>> getAllStock() {
        return ResponseEntity.ok(stockService.getAllStock());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Consultar stock de un producto",
               description = "El productId es el ID del producto en sales-microservices")
    @ApiResponse(responseCode = "200", description = "Stock encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no registrado en inventario")
    public ResponseEntity<ProductStockResponse> getStockByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(stockService.getStockByProductId(productId));
    }

    @GetMapping("/{productId}/movements")
    @Operation(summary = "Historial de movimientos de un producto",
               description = "Retorna todos los descuentos de stock aplicados a ese producto, ordenados por fecha desc")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos")
    public ResponseEntity<List<StockMovementResponse>> getMovementsByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(stockService.getMovementsByProductId(productId));
    }

    @GetMapping("/by-sale/{saleId}")
    @Operation(summary = "Movimientos generados por una venta",
               description = "Permite correlacionar los descuentos de stock con la venta original")
    @ApiResponse(responseCode = "200", description = "Movimientos de esa venta")
    public ResponseEntity<List<StockMovementResponse>> getMovementsBySale(@PathVariable String saleId) {
        return ResponseEntity.ok(stockService.getMovementsBySaleId(saleId));
    }
}
