package com.juanfedevmaster.inventorymicroservice.repository;

import com.juanfedevmaster.inventorymicroservice.domain.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    boolean existsByEventIdAndProductId(String eventId, String productId);

    List<StockMovement> findByProductIdOrderByMovedAtDesc(String productId);

    List<StockMovement> findBySaleId(String saleId);
}
