package com.incubyte.salarymanagement.dto.response;

import com.incubyte.salarymanagement.enums.EmployeeStatus;

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
        String state,
        String address,
        String currency,
        LocalDate hireDate,
        EmployeeStatus status,
        BigDecimal currentSalaryAmount,
        String currentSalaryCurrency
) {
}
