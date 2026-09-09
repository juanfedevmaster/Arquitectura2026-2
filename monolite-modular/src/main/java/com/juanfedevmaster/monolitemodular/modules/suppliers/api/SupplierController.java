package com.juanfedevmaster.monolitemodular.modules.suppliers.api;

import com.juanfedevmaster.monolitemodular.modules.suppliers.api.dto.SupplierRequest;
import com.juanfedevmaster.monolitemodular.modules.suppliers.api.dto.SupplierResponse;
import com.juanfedevmaster.monolitemodular.modules.suppliers.application.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Supplier management")
public class SupplierController {

    private final SupplierService service;

    @GetMapping
    @Operation(summary = "List all active suppliers")
    public List<SupplierResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public SupplierResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create supplier")
    public SupplierResponse create(@Valid @RequestBody SupplierRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier")
    public SupplierResponse update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate supplier (soft delete)")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
