package com.juanfedevmaster.monolitemodular.modules.reports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesPeriodReport(
    LocalDateTime from,
    LocalDateTime to,
    Long transactionCount,
    BigDecimal totalRevenue
) {}
