package com.tienda.catalog.service;

import com.tienda.catalog.dto.ProductRequest;
import com.tienda.catalog.dto.ProductResponse;
import com.tienda.catalog.model.Category;
import com.tienda.catalog.model.Product;
import com.tienda.catalog.repository.CategoryRepository;
import com.tienda.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> findAll(String name, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products;
        if (name != null) {
            products = productRepository.findByActiveTrueAndNameContainingIgnoreCase(name);
        } else if (categoryId != null) {
            products = productRepository.findByActiveTrueAndCategoryId(categoryId);
        } else if (minPrice != null && maxPrice != null) {
            products = productRepository.findByActiveTrueAndPriceBetween(minPrice, maxPrice);
        } else {
            products = productRepository.findByActiveTrue();
        }
        return products.stream().map(ProductResponse::new).toList();
    }

    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return new ProductResponse(product);
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        fillProduct(product, request);
        return new ProductResponse(productRepository.save(product));
    }

    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        fillProduct(product, request);
        return new ProductResponse(productRepository.save(product));
    }

    public void delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        product.setActive(false);
        productRepository.save(product);
    }

    private void fillProduct(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            product.setCategory(category);
        }
    }
}
