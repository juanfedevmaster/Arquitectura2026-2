package com.tienda.catalog.repository;

import com.tienda.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByActiveTrueAndNameContainingIgnoreCase(String name);
    List<Product> findByActiveTrueAndCategoryId(Integer categoryId);
    List<Product> findByActiveTrueAndPriceBetween(BigDecimal min, BigDecimal max);
    List<Product> findByActiveTrue();
}
