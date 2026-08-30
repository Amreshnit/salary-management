package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.domain.Employee;
import com.incubyte.salarymanagement.repository.EmployeeRepository;
import com.incubyte.salarymanagement.web.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class EmployeeLookup {

    private final EmployeeRepository employeeRepository;

    public EmployeeLookup(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee findByIdOrThrow(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    public boolean existsById(Long employeeId) {
        return employeeRepository.existsById(employeeId);
    }
}
