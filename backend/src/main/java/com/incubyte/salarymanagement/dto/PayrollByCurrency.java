package com.incubyte.salarymanagement.dto;

import java.math.BigDecimal;

public record PayrollByCurrency(
        String currency,
        BigDecimal totalAnnualCost,
        long employeeCount
) {
}
