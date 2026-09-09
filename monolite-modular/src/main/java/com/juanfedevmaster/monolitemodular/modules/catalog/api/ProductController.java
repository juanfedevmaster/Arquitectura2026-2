package com.juanfedevmaster.monolitemodular.modules.catalog.api;

import com.juanfedevmaster.monolitemodular.modules.catalog.api.dto.ProductRequest;
import com.juanfedevmaster.monolitemodular.modules.catalog.api.dto.ProductResponse;
import com.juanfedevmaster.monolitemodular.modules.catalog.application.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management")
public class ProductController {

    private final ProductService service;

    @GetMapping
    @Operation(summary = "List all active products")
    public List<ProductResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ProductResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public List<ProductResponse> search(@RequestParam String name) {
        return service.search(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create product")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate product (soft delete)")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
