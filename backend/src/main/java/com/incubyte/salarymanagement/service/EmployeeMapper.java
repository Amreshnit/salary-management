package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.domain.Employee;
import com.incubyte.salarymanagement.domain.SalaryRecord;
import com.incubyte.salarymanagement.dto.EmployeeResponse;
import com.incubyte.salarymanagement.dto.SalaryRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee, SalaryRecord currentSalary) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getSeniorityLevel(),
                employee.getCountry(),
                employee.getCurrency(),
                employee.getHireDate(),
                employee.getStatus(),
                currentSalary != null ? currentSalary.getAmount() : null,
                currentSalary != null ? currentSalary.getCurrency() : null
        );
    }

    public SalaryRecordResponse toResponse(SalaryRecord salaryRecord) {
        return new SalaryRecordResponse(
                salaryRecord.getId(),
                salaryRecord.getAmount(),
                salaryRecord.getCurrency(),
                salaryRecord.getEffectiveFrom(),
                salaryRecord.getEffectiveTo(),
                salaryRecord.getReason()
        );
    }
}
