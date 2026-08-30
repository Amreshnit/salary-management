package com.incubyte.salarymanagement.dto;

import java.math.BigDecimal;

public record CountrySalaryStat(
        String country,
        String currency,
        BigDecimal averageAmount,
        long employeeCount
) {
}
