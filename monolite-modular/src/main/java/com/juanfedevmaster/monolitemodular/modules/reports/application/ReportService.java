package com.juanfedevmaster.monolitemodular.modules.reports.application;

import com.juanfedevmaster.monolitemodular.modules.catalog.infrastructure.ProductRepository;
import com.juanfedevmaster.monolitemodular.modules.reports.api.dto.*;
import com.juanfedevmaster.monolitemodular.modules.sales.infrastructure.SaleItemRepository;
import com.juanfedevmaster.monolitemodular.modules.sales.infrastructure.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public SalesPeriodReport getSalesByPeriod(LocalDateTime from, LocalDateTime to) {
        BigDecimal total = saleRepository.sumTotalByPeriod(from, to);
        Long count = saleRepository.countByPeriod(from, to);
        return new SalesPeriodReport(
            from, to,
            count != null ? count : 0L,
            total != null ? total : BigDecimal.ZERO
        );
    }

    @Transactional(readOnly = true)
    public List<TopProductReport> getTopProducts(LocalDateTime from, LocalDateTime to) {
        return saleItemRepository.findTopProductsByPeriod(from, to).stream()
            .map(row -> new TopProductReport(
                (Long) row[0],
                (String) row[1],
                ((Number) row[2]).longValue(),
                (BigDecimal) row[3]
            )).toList();
    }

    @Transactional(readOnly = true)
    public List<LowStockReport> getLowStockReport() {
        return productRepository.findLowStockProducts().stream()
            .map(p -> new LowStockReport(
                p.getId(), p.getSku(), p.getName(),
                p.getStockQuantity(), p.getMinStock()
            )).toList();
    }

    @Transactional(readOnly = true)
    public ProfitReport getProfitByPeriod(LocalDateTime from, LocalDateTime to) {
        BigDecimal profit = saleItemRepository.calculateProfitByPeriod(from, to);
        return new ProfitReport(from, to, profit != null ? profit : BigDecimal.ZERO);
    }
}
