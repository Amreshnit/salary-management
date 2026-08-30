package com.incubyte.salarymanagement.dto.response;

import java.math.BigDecimal;

public record HeadcountSummary(
        long activeEmployees,
        long inactiveEmployees,
        long departments,
        long countries,
        BigDecimal averageTenureYears,
        long newHiresLast90Days
) {
}
