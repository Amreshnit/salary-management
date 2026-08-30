package com.incubyte.salarymanagement.dto;

import com.incubyte.salarymanagement.domain.SalaryChangeReason;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryRecordResponse(
        Long id,
        BigDecimal amount,
        String currency,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        SalaryChangeReason reason
) {
}
