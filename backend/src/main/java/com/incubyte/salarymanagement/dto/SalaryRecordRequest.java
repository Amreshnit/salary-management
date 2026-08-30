package com.incubyte.salarymanagement.dto;

import com.incubyte.salarymanagement.domain.SalaryChangeReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryRecordRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull @Size(min = 3, max = 3) String currency,
        @NotNull LocalDate effectiveFrom,
        @NotNull SalaryChangeReason reason
) {
}
