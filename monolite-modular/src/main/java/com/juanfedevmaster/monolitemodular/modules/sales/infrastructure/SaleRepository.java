package com.juanfedevmaster.monolitemodular.modules.sales.infrastructure;

import com.juanfedevmaster.monolitemodular.modules.sales.domain.Sale;
import com.juanfedevmaster.monolitemodular.modules.sales.domain.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findBySaleDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, SaleStatus status);

    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'ACTIVE'")
    BigDecimal sumTotalByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'ACTIVE'")
    Long countByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
