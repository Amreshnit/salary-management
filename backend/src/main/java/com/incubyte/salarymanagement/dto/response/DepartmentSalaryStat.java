package com.incubyte.salarymanagement.dto.response;

import java.math.BigDecimal;

public record DepartmentSalaryStat(
        String department,
        String currency,
        BigDecimal averageAmount,
        long employeeCount
) {
}
