package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.domain.Employee;
import com.incubyte.salarymanagement.domain.SalaryChangeReason;
import com.incubyte.salarymanagement.domain.SalaryRecord;
import com.incubyte.salarymanagement.dto.SalaryRecordRequest;
import com.incubyte.salarymanagement.dto.SalaryRecordResponse;
import com.incubyte.salarymanagement.repository.SalaryRecordRepository;
import com.incubyte.salarymanagement.web.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceImplTest {

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @Mock
    private EmployeeLookup employeeLookup;

    private SalaryServiceImpl salaryService;

    @BeforeEach
    void setUp() {
        salaryService = new SalaryServiceImpl(salaryRecordRepository, employeeLookup, new EmployeeMapper());
    }

    @Test
    void addSalaryRecordClosesThePreviouslyOpenRecordBeforeTheNewEffectiveDate() {
        Employee employee = Employee.builder().id(1L).build();
        when(employeeLookup.findByIdOrThrow(1L)).thenReturn(employee);

        SalaryRecord openRecord = SalaryRecord.builder()
                .id(10L)
                .amount(new BigDecimal("90000.00"))
                .currency("USD")
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .effectiveTo(null)
                .reason(SalaryChangeReason.HIRE)
                .build();
        when(salaryRecordRepository.findCurrentByEmployeeId(1L)).thenReturn(Optional.of(openRecord));
        when(salaryRecordRepository.save(any(SalaryRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SalaryRecordRequest raiseRequest = new SalaryRecordRequest(new BigDecimal("100000.00"), "USD",
                LocalDate.of(2025, 1, 1), SalaryChangeReason.RAISE);

        SalaryRecordResponse response = salaryService.addSalaryRecord(1L, raiseRequest);

        assertThat(openRecord.getEffectiveTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(response.amount()).isEqualByComparingTo("100000.00");
        assertThat(response.reason()).isEqualTo(SalaryChangeReason.RAISE);

        ArgumentCaptor<SalaryRecord> savedRecords = ArgumentCaptor.forClass(SalaryRecord.class);
        verify(salaryRecordRepository, times(2)).save(savedRecords.capture());
        assertThat(savedRecords.getAllValues()).hasSize(2);
    }

    @Test
    void addSalaryRecordDoesNotFailWhenEmployeeHasNoPriorSalaryRecord() {
        Employee employee = Employee.builder().id(2L).build();
        when(employeeLookup.findByIdOrThrow(2L)).thenReturn(employee);
        when(salaryRecordRepository.findCurrentByEmployeeId(2L)).thenReturn(Optional.empty());
        when(salaryRecordRepository.save(any(SalaryRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SalaryRecordRequest hireRequest = new SalaryRecordRequest(new BigDecimal("70000.00"), "USD",
                LocalDate.of(2025, 6, 1), SalaryChangeReason.HIRE);

        SalaryRecordResponse response = salaryService.addSalaryRecord(2L, hireRequest);

        assertThat(response.amount()).isEqualByComparingTo("70000.00");
        verify(salaryRecordRepository, times(1)).save(any(SalaryRecord.class));
    }

    @Test
    void getSalaryHistoryThrowsWhenEmployeeDoesNotExist() {
        when(employeeLookup.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> salaryService.getSalaryHistory(999L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void getSalaryHistoryReturnsRecordsMostRecentFirst() {
        when(employeeLookup.existsById(1L)).thenReturn(true);
        SalaryRecord older = SalaryRecord.builder().id(1L).amount(BigDecimal.TEN).currency("USD")
                .effectiveFrom(LocalDate.of(2023, 1, 1)).reason(SalaryChangeReason.HIRE).build();
        SalaryRecord newer = SalaryRecord.builder().id(2L).amount(BigDecimal.TEN).currency("USD")
                .effectiveFrom(LocalDate.of(2024, 1, 1)).reason(SalaryChangeReason.RAISE).build();
        when(salaryRecordRepository.findByEmployeeIdOrderByEffectiveFromDesc(1L)).thenReturn(List.of(newer, older));

        List<SalaryRecordResponse> history = salaryService.getSalaryHistory(1L);

        assertThat(history).extracting(SalaryRecordResponse::id).containsExactly(2L, 1L);
    }
}
