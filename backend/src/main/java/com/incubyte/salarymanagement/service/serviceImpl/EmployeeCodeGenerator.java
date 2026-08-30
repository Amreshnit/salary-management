package com.incubyte.salarymanagement.service.serviceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmployeeCodeGenerator {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeCodeGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String nextEmployeeCode() {
        Long nextValue = jdbcTemplate.queryForObject("SELECT nextval('employee_code_seq')", Long.class);
        return "EMP-" + nextValue;
    }
}
