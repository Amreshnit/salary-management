package com.incubyte.salarymanagement.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incubyte.salarymanagement.domain.EmployeeStatus;
import com.incubyte.salarymanagement.dto.EmployeeCreateRequest;
import com.incubyte.salarymanagement.dto.EmployeeResponse;
import com.incubyte.salarymanagement.service.EmployeeService;
import com.incubyte.salarymanagement.service.SalaryService;
import com.incubyte.salarymanagement.web.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private SalaryService salaryService;

    @Test
    void searchEmployeesReturnsAPageOfResults() throws Exception {
        EmployeeResponse employee = new EmployeeResponse(1L, "EMP-00001", "Ada", "Lovelace",
                "ada@acme-corp.example", "Engineering", "Senior Software Engineer", "Senior",
                "United States", null, null, "USD", LocalDate.of(2024, 1, 1), EmployeeStatus.ACTIVE,
                new BigDecimal("120000.00"), "USD");
        Page<EmployeeResponse> page = new PageImpl<>(List.of(employee), PageRequest.of(0, 20), 1);
        when(employeeService.searchEmployees(any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].employeeCode").value("EMP-00001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getEmployeeByIdReturns404WhenMissing() throws Exception {
        when(employeeService.getEmployeeById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/api/v1/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createEmployeeRejectsAnInvalidPayloadWithFieldErrors() throws Exception {
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
    }

    @Test
    void createEmployeeReturns201OnSuccess() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest("Ada", "Lovelace", "ada@acme-corp.example",
                "Engineering", "Senior Software Engineer", "Senior", "United States", null, null, "USD",
                LocalDate.of(2024, 1, 1), new BigDecimal("120000.00"));
        EmployeeResponse response = new EmployeeResponse(1L, "EMP-10001", "Ada", "Lovelace",
                "ada@acme-corp.example", "Engineering", "Senior Software Engineer", "Senior",
                "United States", null, null, "USD", LocalDate.of(2024, 1, 1), EmployeeStatus.ACTIVE,
                new BigDecimal("120000.00"), "USD");
        when(employeeService.createEmployee(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeCode").value("EMP-10001"));
    }
}
