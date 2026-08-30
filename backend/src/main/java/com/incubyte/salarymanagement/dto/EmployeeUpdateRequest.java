package com.incubyte.salarymanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String department,
        @NotBlank String jobTitle,
        @NotBlank String seniorityLevel,
        @NotBlank String country,
        @NotBlank @Size(min = 3, max = 3) String currency
) {
}
