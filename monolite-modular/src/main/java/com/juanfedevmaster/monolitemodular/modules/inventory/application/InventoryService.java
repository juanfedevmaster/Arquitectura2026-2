package com.juanfedevmaster.monolitemodular.modules.inventory.application;

import com.juanfedevmaster.monolitemodular.modules.catalog.application.ProductService;
import com.juanfedevmaster.monolitemodular.modules.catalog.domain.Product;
import com.juanfedevmaster.monolitemodular.modules.catalog.infrastructure.ProductRepository;
import com.juanfedevmaster.monolitemodular.modules.inventory.api.dto.*;
import com.juanfedevmaster.monolitemodular.modules.inventory.domain.InventoryEntry;
import com.juanfedevmaster.monolitemodular.modules.inventory.domain.InventoryExit;
import com.juanfedevmaster.monolitemodular.modules.inventory.infrastructure.InventoryEntryRepository;
import com.juanfedevmaster.monolitemodular.modules.inventory.infrastructure.InventoryExitRepository;
import com.juanfedevmaster.monolitemodular.modules.suppliers.domain.Supplier;
import com.juanfedevmaster.monolitemodular.modules.suppliers.infrastructure.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryEntryRepository entryRepository;
    private final InventoryExitRepository exitRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public InventoryEntryResponse registerEntry(InventoryEntryRequest request) {
        Product product = productService.getOrThrow(request.productId());

        Supplier supplier = null;
        if (request.supplierId() != null) {
            supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + request.supplierId()));
        }

        product.addStock(request.quantity());
        productRepository.save(product);

        InventoryEntry entry = InventoryEntry.builder()
            .product(product)
            .quantity(request.quantity())
            .cost(request.cost())
            .entryDate(request.entryDate() != null ? request.entryDate() : LocalDateTime.now())
            .supplier(supplier)
            .build();

        return toEntryResponse(entryRepository.save(entry));
    }

    @Transactional
    public InventoryExitResponse registerExit(InventoryExitRequest request) {
        Product product = productService.getOrThrow(request.productId());
        product.deductStock(request.quantity());
        productRepository.save(product);

        InventoryExit exit = InventoryExit.builder()
            .product(product)
            .quantity(request.quantity())
            .reason(request.reason())
            .notes(request.notes())
            .exitDate(LocalDateTime.now())
            .build();

        return toExitResponse(exitRepository.save(exit));
    }

    @Transactional(readOnly = true)
    public StockResponse getStock(Long productId) {
        Product product = productService.getOrThrow(productId);
        return toStockResponse(product);
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
            .map(this::toStockResponse)
            .toList();
    }

    private StockResponse toStockResponse(Product p) {
        return new StockResponse(p.getId(), p.getSku(), p.getName(), p.getStockQuantity(), p.getMinStock(), p.isBelowMinStock());
    }

    private InventoryEntryResponse toEntryResponse(InventoryEntry e) {
        return new InventoryEntryResponse(
            e.getId(),
            e.getProduct().getId(), e.getProduct().getName(),
            e.getQuantity(), e.getCost(), e.getEntryDate(),
            e.getSupplier() != null ? e.getSupplier().getId() : null,
            e.getSupplier() != null ? e.getSupplier().getName() : null
        );
    }

    private InventoryExitResponse toExitResponse(InventoryExit e) {
        return new InventoryExitResponse(
            e.getId(),
            e.getProduct().getId(), e.getProduct().getName(),
            e.getQuantity(), e.getReason(), e.getNotes(), e.getExitDate()
        );
    }
}
