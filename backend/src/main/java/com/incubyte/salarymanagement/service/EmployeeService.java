package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.domain.EmployeeStatus;
import com.incubyte.salarymanagement.dto.EmployeeCreateRequest;
import com.incubyte.salarymanagement.dto.EmployeeResponse;
import com.incubyte.salarymanagement.dto.EmployeeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Page<EmployeeResponse> searchEmployees(String department, String country, EmployeeStatus status, String search,
                                            Pageable pageable);

    EmployeeResponse getEmployeeById(Long employeeId);

    EmployeeResponse createEmployee(EmployeeCreateRequest request);

    EmployeeResponse updateEmployee(Long employeeId, EmployeeUpdateRequest request);

    void deactivateEmployee(Long employeeId);
}
