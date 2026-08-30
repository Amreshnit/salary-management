package com.incubyte.salarymanagement.dto.request;

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
        String state,
        String address,
        @NotBlank @Size(min = 3, max = 3) String currency
) {
}
