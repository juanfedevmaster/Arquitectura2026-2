package com.juanfedevmaster.monolitemodular.modules.sales.infrastructure;

import com.juanfedevmaster.monolitemodular.modules.sales.domain.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    @Query("""
        SELECT si.product.id, si.product.name, SUM(si.quantity), SUM(si.subtotal)
        FROM SaleItem si
        JOIN si.sale s
        WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'ACTIVE'
        GROUP BY si.product.id, si.product.name
        ORDER BY SUM(si.quantity) DESC
        """)
    List<Object[]> findTopProductsByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
        SELECT SUM((si.unitPrice - si.product.purchasePrice) * si.quantity)
        FROM SaleItem si
        JOIN si.sale s
        WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'ACTIVE'
        """)
    BigDecimal calculateProfitByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
