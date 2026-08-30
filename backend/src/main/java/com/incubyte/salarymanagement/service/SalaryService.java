package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.model.Employee;
import com.incubyte.salarymanagement.enums.SalaryChangeReason;
import com.incubyte.salarymanagement.model.SalaryRecord;
import com.incubyte.salarymanagement.dto.request.SalaryRecordRequest;
import com.incubyte.salarymanagement.dto.response.SalaryRecordResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SalaryService {

    List<SalaryRecordResponse> getSalaryHistory(Long employeeId);

    SalaryRecordResponse addSalaryRecord(Long employeeId, SalaryRecordRequest request);

    SalaryRecord openInitialSalaryRecord(Employee employee, BigDecimal amount, String currency, LocalDate effectiveFrom, SalaryChangeReason reason);
}
