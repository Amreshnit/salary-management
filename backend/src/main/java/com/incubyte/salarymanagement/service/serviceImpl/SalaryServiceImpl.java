package com.incubyte.salarymanagement.service.serviceImpl;

import com.incubyte.salarymanagement.domain.Employee;
import com.incubyte.salarymanagement.domain.SalaryChangeReason;
import com.incubyte.salarymanagement.domain.SalaryRecord;
import com.incubyte.salarymanagement.dto.SalaryRecordRequest;
import com.incubyte.salarymanagement.dto.SalaryRecordResponse;
import com.incubyte.salarymanagement.repository.SalaryRecordRepository;
import com.incubyte.salarymanagement.service.SalaryService;
import com.incubyte.salarymanagement.web.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRecordRepository salaryRecordRepository;
    private final EmployeeLookup employeeLookup;
    private final EmployeeMapper employeeMapper;

    public SalaryServiceImpl(SalaryRecordRepository salaryRecordRepository, EmployeeLookup employeeLookup,
                              EmployeeMapper employeeMapper) {
        this.salaryRecordRepository = salaryRecordRepository;
        this.employeeLookup = employeeLookup;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryRecordResponse> getSalaryHistory(Long employeeId) {
        if (!employeeLookup.existsById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return salaryRecordRepository.findByEmployeeIdOrderByEffectiveFromDesc(employeeId).stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SalaryRecordResponse addSalaryRecord(Long employeeId, SalaryRecordRequest request) {
        Employee employee = employeeLookup.findByIdOrThrow(employeeId);
        closeCurrentSalaryRecord(employee.getId(), request.effectiveFrom());

        SalaryRecord newRecord = SalaryRecord.builder()
                .employee(employee)
                .amount(request.amount())
                .currency(request.currency())
                .effectiveFrom(request.effectiveFrom())
                .reason(request.reason())
                .build();

        return employeeMapper.toResponse(salaryRecordRepository.save(newRecord));
    }

    @Override
    @Transactional
    public SalaryRecord openInitialSalaryRecord(Employee employee, BigDecimal amount, String currency,
                                                 LocalDate effectiveFrom, SalaryChangeReason reason) {
        SalaryRecord record = SalaryRecord.builder()
                .employee(employee)
                .amount(amount)
                .currency(currency)
                .effectiveFrom(effectiveFrom)
                .reason(reason)
                .build();
        return salaryRecordRepository.save(record);
    }

    private void closeCurrentSalaryRecord(Long employeeId, LocalDate newEffectiveFrom) {
        Optional<SalaryRecord> currentRecord = salaryRecordRepository.findCurrentByEmployeeId(employeeId);
        currentRecord.ifPresent(record -> {
            record.setEffectiveTo(newEffectiveFrom.minusDays(1));
            salaryRecordRepository.save(record);
        });
    }
}
