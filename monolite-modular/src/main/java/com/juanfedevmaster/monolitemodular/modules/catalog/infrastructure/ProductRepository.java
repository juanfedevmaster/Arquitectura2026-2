package com.juanfedevmaster.monolitemodular.modules.catalog.infrastructure;

import com.juanfedevmaster.monolitemodular.modules.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Product> findByActiveTrue();
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStock AND p.active = true")
    List<Product> findLowStockProducts();
}
