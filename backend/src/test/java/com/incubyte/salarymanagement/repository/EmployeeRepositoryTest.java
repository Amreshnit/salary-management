package com.incubyte.salarymanagement.repository;

import com.incubyte.salarymanagement.model.Employee;
import com.incubyte.salarymanagement.enums.EmployeeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void searchFiltersByDepartmentCountryStatusAndFreeTextTogether() {
        saveEmployee("EMP-T001", "Ada", "Lovelace", "ada.t@acme-corp.example", "Engineering", "United States", EmployeeStatus.ACTIVE);
        saveEmployee("EMP-T002", "Grace", "Hopper", "grace.t@acme-corp.example", "Engineering", "India", EmployeeStatus.ACTIVE);
        saveEmployee("EMP-T003", "Alan", "Turing", "alan.t@acme-corp.example", "Sales", "United States", EmployeeStatus.INACTIVE);

        Page<Employee> engineeringInUs = employeeRepository.search("Engineering", "United States", EmployeeStatus.ACTIVE,
                null, PageRequest.of(0, 10));

        assertThat(engineeringInUs.getContent()).extracting(Employee::getEmployeeCode).containsExactly("EMP-T001");
    }

    @Test
    void searchMatchesFreeTextAgainstNameEmailAndEmployeeCode() {
        saveEmployee("EMP-T010", "Katherine", "Johnson", "katherine.t@acme-corp.example", "Engineering", "United States", EmployeeStatus.ACTIVE);

        Page<Employee> byLastName = employeeRepository.search(null, null, null, "johnson", PageRequest.of(0, 10));
        Page<Employee> byCode = employeeRepository.search(null, null, null, "EMP-T010", PageRequest.of(0, 10));

        assertThat(byLastName.getContent()).hasSize(1);
        assertThat(byCode.getContent()).hasSize(1);
    }

    @Test
    void findDistinctDepartmentsAndCountriesReturnsSortedUniqueValues() {
        saveEmployee("EMP-T030", "Ada", "Lovelace", "ada.t2@acme-corp.example", "Engineering", "United States", EmployeeStatus.ACTIVE);
        saveEmployee("EMP-T031", "Grace", "Hopper", "grace.t2@acme-corp.example", "Engineering", "India", EmployeeStatus.ACTIVE);
        saveEmployee("EMP-T032", "Alan", "Turing", "alan.t2@acme-corp.example", "Sales", "France", EmployeeStatus.ACTIVE);

        assertThat(employeeRepository.findDistinctDepartments()).containsExactly("Engineering", "Sales");
        assertThat(employeeRepository.findDistinctCountries()).containsExactly("France", "India", "United States");
    }

    @Test
    void existsByEmailReflectsSavedEmployees() {
        saveEmployee("EMP-T020", "Margaret", "Hamilton", "margaret.t@acme-corp.example", "Engineering", "United States", EmployeeStatus.ACTIVE);

        assertThat(employeeRepository.existsByEmail("margaret.t@acme-corp.example")).isTrue();
        assertThat(employeeRepository.existsByEmail("nobody@acme-corp.example")).isFalse();
    }

    private void saveEmployee(String employeeCode, String firstName, String lastName, String email,
                              String department, String country, EmployeeStatus status) {
        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .department(department)
                .jobTitle("Software Engineer")
                .seniorityLevel("Senior")
                .country(country)
                .currency("USD")
                .hireDate(LocalDate.of(2024, 1, 1))
                .status(status)
                .build();
        employeeRepository.save(employee);
    }
}
