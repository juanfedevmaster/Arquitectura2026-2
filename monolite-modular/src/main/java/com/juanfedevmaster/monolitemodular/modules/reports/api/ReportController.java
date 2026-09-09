package com.juanfedevmaster.monolitemodular.modules.reports.api;

import com.juanfedevmaster.monolitemodular.modules.reports.api.dto.*;
import com.juanfedevmaster.monolitemodular.modules.reports.application.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Business intelligence reports")
public class ReportController {

    private final ReportService service;

    @GetMapping("/sales")
    @Operation(summary = "Sales summary by period (revenue + transaction count)")
    public SalesPeriodReport getSalesByPeriod(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return service.getSalesByPeriod(from, to);
    }

    @GetMapping("/top-products")
    @Operation(summary = "Top selling products by period")
    public List<TopProductReport> getTopProducts(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return service.getTopProducts(from, to);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Products at or below minimum stock")
    public List<LowStockReport> getLowStockReport() {
        return service.getLowStockReport();
    }

    @GetMapping("/profit")
    @Operation(summary = "Estimated profit by period (sale price − purchase price × qty)")
    public ProfitReport getProfitByPeriod(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return service.getProfitByPeriod(from, to);
    }
}
