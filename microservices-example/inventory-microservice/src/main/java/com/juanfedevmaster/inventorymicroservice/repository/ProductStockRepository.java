package com.juanfedevmaster.inventorymicroservice.repository;

import com.juanfedevmaster.inventorymicroservice.domain.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

    Optional<ProductStock> findByProductId(String productId);
}
