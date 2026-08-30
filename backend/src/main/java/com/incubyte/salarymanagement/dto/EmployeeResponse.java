package com.incubyte.salarymanagement.dto;

import com.incubyte.salarymanagement.domain.EmployeeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String department,
        String jobTitle,
        String seniorityLevel,
        String country,
        String currency,
        LocalDate hireDate,
        EmployeeStatus status,
        BigDecimal currentSalaryAmount,
        String currentSalaryCurrency
) {
}
