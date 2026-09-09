package com.juanfedevmaster.monolitemodular.modules.catalog.application;

import com.juanfedevmaster.monolitemodular.modules.catalog.api.dto.ProductRequest;
import com.juanfedevmaster.monolitemodular.modules.catalog.api.dto.ProductResponse;
import com.juanfedevmaster.monolitemodular.modules.catalog.domain.Category;
import com.juanfedevmaster.monolitemodular.modules.catalog.domain.Product;
import com.juanfedevmaster.monolitemodular.modules.catalog.infrastructure.CategoryRepository;
import com.juanfedevmaster.monolitemodular.modules.catalog.infrastructure.ProductRepository;
import com.juanfedevmaster.monolitemodular.shared.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> search(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name)
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new BusinessException("SKU already exists: " + request.sku());
        }
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.categoryId()));
        Product product = Product.builder()
            .sku(request.sku())
            .name(request.name())
            .category(category)
            .unitMeasure(request.unitMeasure())
            .purchasePrice(request.purchasePrice())
            .salePrice(request.salePrice())
            .minStock(request.minStock())
            .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.categoryId()));
        product.setSku(request.sku());
        product.setName(request.name());
        product.setCategory(category);
        product.setUnitMeasure(request.unitMeasure());
        product.setPurchasePrice(request.purchasePrice());
        product.setSalePrice(request.salePrice());
        product.setMinStock(request.minStock());
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = getOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public Product getOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
            p.getId(), p.getSku(), p.getName(),
            p.getCategory().getId(), p.getCategory().getName(),
            p.getUnitMeasure(),
            p.getPurchasePrice(), p.getSalePrice(),
            p.getMinStock(), p.getStockQuantity(),
            p.isActive(), p.isBelowMinStock()
        );
    }
}
