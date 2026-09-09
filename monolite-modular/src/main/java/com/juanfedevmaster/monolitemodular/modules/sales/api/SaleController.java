package com.juanfedevmaster.monolitemodular.modules.sales.api;

import com.juanfedevmaster.monolitemodular.modules.sales.api.dto.SaleRequest;
import com.juanfedevmaster.monolitemodular.modules.sales.api.dto.SaleResponse;
import com.juanfedevmaster.monolitemodular.modules.sales.application.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Point of sale — create and void sales")
public class SaleController {

    private final SaleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new sale — auto-deducts inventory")
    public SaleResponse create(@Valid @RequestBody SaleRequest request) {
        return service.createSale(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale receipt by ID")
    public SaleResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/{id}/void")
    @Operation(summary = "Void a sale — restores inventory")
    public SaleResponse voidSale(@PathVariable Long id) {
        return service.voidSale(id);
    }
}
