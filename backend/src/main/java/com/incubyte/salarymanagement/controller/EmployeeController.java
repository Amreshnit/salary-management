package com.incubyte.salarymanagement.controller;

import com.incubyte.salarymanagement.dto.request.EmployeeCreateRequest;
import com.incubyte.salarymanagement.dto.request.EmployeeUpdateRequest;
import com.incubyte.salarymanagement.dto.request.SalaryRecordRequest;
import com.incubyte.salarymanagement.dto.response.EmployeeResponse;
import com.incubyte.salarymanagement.dto.response.SalaryRecordResponse;
import com.incubyte.salarymanagement.enums.EmployeeStatus;
import com.incubyte.salarymanagement.service.EmployeeService;
import com.incubyte.salarymanagement.service.SalaryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        log.info("Searching employees: department={}, country={}, status={}, q={}, page={}",
                department, country, status, q, pageable.getPageNumber());
        try {
            return employeeService.searchEmployees(department, country, status, q, pageable);
        } catch (Exception ex) {
            log.error("Failed to search employees", ex);
            throw ex;
        }
    }

    @GetMapping("/{employeeId}")
    public EmployeeResponse getEmployeeById(@PathVariable Long employeeId) {
        log.info("Fetching employee {}", employeeId);
        try {
            return employeeService.getEmployeeById(employeeId);
        } catch (Exception ex) {
            log.error("Failed to fetch employee {}", employeeId, ex);
            throw ex;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        log.info("Creating employee with email {}", request.email());
        try {
            EmployeeResponse response = employeeService.createEmployee(request);
            log.info("Created employee {} with code {}", response.id(), response.employeeCode());
            return response;
        } catch (Exception ex) {
            log.error("Failed to create employee with email {}", request.email(), ex);
            throw ex;
        }
    }

    @PutMapping("/{employeeId}")
    public EmployeeResponse updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody EmployeeUpdateRequest request) {
        log.info("Updating employee {}", employeeId);
        try {
            return employeeService.updateEmployee(employeeId, request);
        } catch (Exception ex) {
            log.error("Failed to update employee {}", employeeId, ex);
            throw ex;
        }
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable Long employeeId) {
        log.info("Deactivating employee {}", employeeId);
        try {
            employeeService.deactivateEmployee(employeeId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            log.error("Failed to deactivate employee {}", employeeId, ex);
            throw ex;
        }
    }

    @GetMapping("/{employeeId}/salary-history")
    public List<SalaryRecordResponse> getSalaryHistory(@PathVariable Long employeeId) {
        log.info("Fetching salary history for employee {}", employeeId);
        try {
            return salaryService.getSalaryHistory(employeeId);
        } catch (Exception ex) {
            log.error("Failed to fetch salary history for employee {}", employeeId, ex);
            throw ex;
        }
    }

    @PostMapping("/{employeeId}/salary-records")
    @ResponseStatus(HttpStatus.CREATED)
    public SalaryRecordResponse addSalaryRecord(@PathVariable Long employeeId, @Valid @RequestBody SalaryRecordRequest request) {
        log.info("Adding salary record for employee {}: amount={}, reason={}", employeeId, request.amount(), request.reason());
        try {
            return salaryService.addSalaryRecord(employeeId, request);
        } catch (Exception ex) {
            log.error("Failed to add salary record for employee {}", employeeId, ex);
            throw ex;
        }
    }
}
