package com.incubyte.salarymanagement.dto.response;

import java.math.BigDecimal;

public record PayrollByCurrency(
        String currency,
        BigDecimal totalAnnualCost,
        long employeeCount
) {
}
