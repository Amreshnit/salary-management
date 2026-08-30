package com.incubyte.salarymanagement.service.serviceImpl;

import com.incubyte.salarymanagement.model.Employee;
import com.incubyte.salarymanagement.enums.EmployeeStatus;
import com.incubyte.salarymanagement.enums.SalaryChangeReason;
import com.incubyte.salarymanagement.model.SalaryRecord;
import com.incubyte.salarymanagement.dto.request.EmployeeCreateRequest;
import com.incubyte.salarymanagement.dto.response.EmployeeResponse;
import com.incubyte.salarymanagement.dto.request.EmployeeUpdateRequest;
import com.incubyte.salarymanagement.repository.EmployeeRepository;
import com.incubyte.salarymanagement.repository.SalaryRecordRepository;
import com.incubyte.salarymanagement.service.SalaryService;
import com.incubyte.salarymanagement.exception.DuplicateEmployeeException;
import com.incubyte.salarymanagement.exception.EmployeeNotFoundException;
import com.incubyte.salarymanagement.util.EmployeeCodeGenerator;
import com.incubyte.salarymanagement.util.EmployeeLookup;
import com.incubyte.salarymanagement.util.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @Mock
    private SalaryService salaryService;

    @Mock
    private EmployeeCodeGenerator employeeCodeGenerator;

    @Mock
    private EmployeeLookup employeeLookup;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, salaryRecordRepository, salaryService,
                employeeCodeGenerator, new EmployeeMapper(), employeeLookup);
    }

    @Test
    void createEmployeeGeneratesACodeAndOpensAnInitialHireSalaryRecord() {
        EmployeeCreateRequest request = new EmployeeCreateRequest("Ada", "Lovelace", "ada@acme-corp.example",
                "Engineering", "Senior Software Engineer", "Senior", "United States", null, null, "USD",
                LocalDate.of(2024, 1, 1), new BigDecimal("120000.00"));

        when(employeeRepository.existsByEmail(request.email())).thenReturn(false);
        when(employeeCodeGenerator.nextEmployeeCode()).thenReturn("EMP-10001");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(1L);
            return employee;
        });
        SalaryRecord hireRecord = SalaryRecord.builder().id(1L).amount(request.startingSalary())
                .currency("USD").effectiveFrom(request.hireDate()).reason(SalaryChangeReason.HIRE).build();
        when(salaryService.openInitialSalaryRecord(any(Employee.class), eq(request.startingSalary()),
                eq("USD"), eq(request.hireDate()), eq(SalaryChangeReason.HIRE))).thenReturn(hireRecord);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertThat(response.employeeCode()).isEqualTo("EMP-10001");
        assertThat(response.currentSalaryAmount()).isEqualByComparingTo("120000.00");
        assertThat(response.status()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void createEmployeeRejectsADuplicateEmail() {
        EmployeeCreateRequest request = new EmployeeCreateRequest("Ada", "Lovelace", "ada@acme-corp.example",
                "Engineering", "Senior Software Engineer", "Senior", "United States", null, null, "USD",
                LocalDate.of(2024, 1, 1), new BigDecimal("120000.00"));
        when(employeeRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(request))
                .isInstanceOf(DuplicateEmployeeException.class);

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void getEmployeeByIdThrowsWhenNotFound() {
        when(employeeLookup.findByIdOrThrow(42L)).thenThrow(new EmployeeNotFoundException(42L));

        assertThatThrownBy(() -> employeeService.getEmployeeById(42L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void deactivateEmployeeSetsStatusToInactive() {
        Employee employee = Employee.builder().id(5L).status(EmployeeStatus.ACTIVE).build();
        when(employeeLookup.findByIdOrThrow(5L)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        employeeService.deactivateEmployee(5L);

        assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        verify(employeeRepository).save(employee);
    }

    @Test
    void activateEmployeeSetsStatusToActive() {
        Employee employee = Employee.builder().id(5L).status(EmployeeStatus.INACTIVE).build();
        when(employeeLookup.findByIdOrThrow(5L)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        employeeService.activateEmployee(5L);

        assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
        verify(employeeRepository).save(employee);
    }

    @Test
    void deleteEmployeeRemovesTheEmployeeRecord() {
        Employee employee = Employee.builder().id(5L).status(EmployeeStatus.ACTIVE).build();
        when(employeeLookup.findByIdOrThrow(5L)).thenReturn(employee);

        employeeService.deleteEmployee(5L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployeeThrowsWhenNotFound() {
        when(employeeLookup.findByIdOrThrow(42L)).thenThrow(new EmployeeNotFoundException(42L));

        assertThatThrownBy(() -> employeeService.deleteEmployee(42L))
                .isInstanceOf(EmployeeNotFoundException.class);

        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void updateEmployeeRejectsAnEmailAlreadyUsedByAnotherEmployee() {
        Employee employee = Employee.builder().id(5L).email("old@acme-corp.example").status(EmployeeStatus.ACTIVE).build();
        when(employeeLookup.findByIdOrThrow(5L)).thenReturn(employee);
        when(employeeRepository.existsByEmail("taken@acme-corp.example")).thenReturn(true);

        EmployeeUpdateRequest request = new EmployeeUpdateRequest("Ada", "Lovelace", "taken@acme-corp.example",
                "Engineering", "Senior Software Engineer", "Senior", "United States", null, null, "USD");

        assertThatThrownBy(() -> employeeService.updateEmployee(5L, request))
                .isInstanceOf(DuplicateEmployeeException.class);
    }

    @Test
    void getDistinctDepartmentsAndCountriesDelegateToRepository() {
        when(employeeRepository.findDistinctDepartments()).thenReturn(List.of("Engineering", "Sales"));
        when(employeeRepository.findDistinctCountries()).thenReturn(List.of("India", "United States"));

        assertThat(employeeService.getDistinctDepartments()).containsExactly("Engineering", "Sales");
        assertThat(employeeService.getDistinctCountries()).containsExactly("India", "United States");
    }
}
