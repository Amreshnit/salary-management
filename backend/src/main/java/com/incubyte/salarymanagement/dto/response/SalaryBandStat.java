package com.incubyte.salarymanagement.dto.response;

import java.math.BigDecimal;

public record SalaryBandStat(
        String currency,
        int band,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        long employeeCount
) {
}
