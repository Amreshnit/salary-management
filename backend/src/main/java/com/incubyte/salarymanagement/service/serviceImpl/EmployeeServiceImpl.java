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
import com.incubyte.salarymanagement.service.EmployeeService;
import com.incubyte.salarymanagement.service.SalaryService;
import com.incubyte.salarymanagement.exception.DuplicateEmployeeException;
import com.incubyte.salarymanagement.util.EmployeeCodeGenerator;
import com.incubyte.salarymanagement.util.EmployeeLookup;
import com.incubyte.salarymanagement.util.EmployeeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final SalaryService salaryService;
    private final EmployeeCodeGenerator employeeCodeGenerator;
    private final EmployeeMapper employeeMapper;
    private final EmployeeLookup employeeLookup;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SalaryRecordRepository salaryRecordRepository,
                                SalaryService salaryService, EmployeeCodeGenerator employeeCodeGenerator,
                                EmployeeMapper employeeMapper, EmployeeLookup employeeLookup) {
        this.employeeRepository = employeeRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.salaryService = salaryService;
        this.employeeCodeGenerator = employeeCodeGenerator;
        this.employeeMapper = employeeMapper;
        this.employeeLookup = employeeLookup;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> searchEmployees(String department, String country, EmployeeStatus status,
                                                   String search, Pageable pageable) {
        return employeeRepository.search(blankToNull(department), blankToNull(country), status, blankToNull(search), pageable)
                .map(employee -> employeeMapper.toResponse(employee, currentSalaryOf(employee)));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long employeeId) {
        Employee employee = employeeLookup.findByIdOrThrow(employeeId);
        return employeeMapper.toResponse(employee, currentSalaryOf(employee));
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmployeeException("An employee with email " + request.email() + " already exists");
        }

        Employee employee = Employee.builder()
                .employeeCode(employeeCodeGenerator.nextEmployeeCode())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .department(request.department())
                .jobTitle(request.jobTitle())
                .seniorityLevel(request.seniorityLevel())
                .country(request.country())
                .state(request.state())
                .address(request.address())
                .currency(request.currency())
                .hireDate(request.hireDate())
                .status(EmployeeStatus.ACTIVE)
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        SalaryRecord initialSalary = salaryService.openInitialSalaryRecord(savedEmployee, request.startingSalary(),
                request.currency(), request.hireDate(), SalaryChangeReason.HIRE);

        return employeeMapper.toResponse(savedEmployee, initialSalary);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long employeeId, EmployeeUpdateRequest request) {
        Employee employee = employeeLookup.findByIdOrThrow(employeeId);

        if (!employee.getEmail().equalsIgnoreCase(request.email()) && employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmployeeException("An employee with email " + request.email() + " already exists");
        }

        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setDepartment(request.department());
        employee.setJobTitle(request.jobTitle());
        employee.setSeniorityLevel(request.seniorityLevel());
        employee.setCountry(request.country());
        employee.setState(request.state());
        employee.setAddress(request.address());
        employee.setCurrency(request.currency());

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee, currentSalaryOf(savedEmployee));
    }

    @Override
    @Transactional
    public void deactivateEmployee(Long employeeId) {
        Employee employee = employeeLookup.findByIdOrThrow(employeeId);
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void activateEmployee(Long employeeId) {
        Employee employee = employeeLookup.findByIdOrThrow(employeeId);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeLookup.findByIdOrThrow(employeeId);
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctDepartments() {
        return employeeRepository.findDistinctDepartments();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctCountries() {
        return employeeRepository.findDistinctCountries();
    }

    private SalaryRecord currentSalaryOf(Employee employee) {
        return salaryRecordRepository.findCurrentByEmployeeId(employee.getId()).orElse(null);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
