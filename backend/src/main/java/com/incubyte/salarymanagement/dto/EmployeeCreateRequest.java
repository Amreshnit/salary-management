package com.incubyte.salarymanagement.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeCreateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String department,
        @NotBlank String jobTitle,
        @NotBlank String seniorityLevel,
        @NotBlank String country,
        String state,
        String address,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotNull @PastOrPresent LocalDate hireDate,
        @NotNull @Positive BigDecimal startingSalary
) {
}
