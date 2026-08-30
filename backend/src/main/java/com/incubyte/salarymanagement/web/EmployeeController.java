package com.incubyte.salarymanagement.web;

import com.incubyte.salarymanagement.domain.EmployeeStatus;
import com.incubyte.salarymanagement.dto.EmployeeCreateRequest;
import com.incubyte.salarymanagement.dto.EmployeeResponse;
import com.incubyte.salarymanagement.dto.EmployeeUpdateRequest;
import com.incubyte.salarymanagement.dto.SalaryRecordRequest;
import com.incubyte.salarymanagement.dto.SalaryRecordResponse;
import com.incubyte.salarymanagement.service.EmployeeService;
import com.incubyte.salarymanagement.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final SalaryService salaryService;

    public EmployeeController(EmployeeService employeeService, SalaryService salaryService) {
        this.employeeService = employeeService;
        this.salaryService = salaryService;
    }

    @GetMapping
    public Page<EmployeeResponse> searchEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String q,
            Pageable pageable) {
        return employeeService.searchEmployees(department, country, status, q, pageable);
    }

    @GetMapping("/{employeeId}")
    public EmployeeResponse getEmployeeById(@PathVariable Long employeeId) {
        return employeeService.getEmployeeById(employeeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return employeeService.createEmployee(request);
    }

    @PutMapping("/{employeeId}")
    public EmployeeResponse updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody EmployeeUpdateRequest request) {
        return employeeService.updateEmployee(employeeId, request);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable Long employeeId) {
        employeeService.deactivateEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{employeeId}/salary-history")
    public List<SalaryRecordResponse> getSalaryHistory(@PathVariable Long employeeId) {
        return salaryService.getSalaryHistory(employeeId);
    }

    @PostMapping("/{employeeId}/salary-records")
    @ResponseStatus(HttpStatus.CREATED)
    public SalaryRecordResponse addSalaryRecord(@PathVariable Long employeeId, @Valid @RequestBody SalaryRecordRequest request) {
        return salaryService.addSalaryRecord(employeeId, request);
    }
}
