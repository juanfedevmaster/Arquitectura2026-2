package com.juanfedevmaster.monolitemodular.modules.inventory.api;

import com.juanfedevmaster.monolitemodular.modules.inventory.api.dto.*;
import com.juanfedevmaster.monolitemodular.modules.inventory.application.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock entries, exits and current stock queries")
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register merchandise entry (purchase from supplier)")
    public InventoryEntryResponse registerEntry(@Valid @RequestBody InventoryEntryRequest request) {
        return service.registerEntry(request);
    }

    @PostMapping("/exits")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register manual inventory exit (loss, damage, expiration)")
    public InventoryExitResponse registerExit(@Valid @RequestBody InventoryExitRequest request) {
        return service.registerExit(request);
    }

    @GetMapping("/stock/{productId}")
    @Operation(summary = "Get current stock for a product")
    public StockResponse getStock(@PathVariable Long productId) {
        return service.getStock(productId);
    }

    @GetMapping("/stock/low")
    @Operation(summary = "List products at or below minimum stock")
    public List<StockResponse> getLowStockProducts() {
        return service.getLowStockProducts();
    }
}
