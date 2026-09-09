package com.juanfedevmaster.monolitemodular.modules.reports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProfitReport(
    LocalDateTime from,
    LocalDateTime to,
    BigDecimal estimatedProfit
) {}
