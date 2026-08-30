package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.enums.EmployeeStatus;
import com.incubyte.salarymanagement.dto.request.EmployeeCreateRequest;
import com.incubyte.salarymanagement.dto.response.EmployeeResponse;
import com.incubyte.salarymanagement.dto.request.EmployeeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    Page<EmployeeResponse> searchEmployees(String department, String country, EmployeeStatus status, String search,
                                            Pageable pageable);

    EmployeeResponse getEmployeeById(Long employeeId);

    EmployeeResponse createEmployee(EmployeeCreateRequest request);

    EmployeeResponse updateEmployee(Long employeeId, EmployeeUpdateRequest request);

    void deactivateEmployee(Long employeeId);

    List<String> getDistinctDepartments();

    List<String> getDistinctCountries();
}
