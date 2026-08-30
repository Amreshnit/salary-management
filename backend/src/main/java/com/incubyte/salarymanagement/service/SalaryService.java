package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.domain.Employee;
import com.incubyte.salarymanagement.domain.SalaryChangeReason;
import com.incubyte.salarymanagement.domain.SalaryRecord;
import com.incubyte.salarymanagement.dto.SalaryRecordRequest;
import com.incubyte.salarymanagement.dto.SalaryRecordResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SalaryService {

    List<SalaryRecordResponse> getSalaryHistory(Long employeeId);

    SalaryRecordResponse addSalaryRecord(Long employeeId, SalaryRecordRequest request);

    SalaryRecord openInitialSalaryRecord(Employee employee, BigDecimal amount, String currency, LocalDate effectiveFrom, SalaryChangeReason reason);
}
