package com.incubyte.salarymanagement.controller;

import com.incubyte.salarymanagement.dto.response.CountrySalaryStat;
import com.incubyte.salarymanagement.dto.response.DepartmentSalaryStat;
import com.incubyte.salarymanagement.dto.response.HeadcountSummary;
import com.incubyte.salarymanagement.dto.response.PayrollByCurrency;
import com.incubyte.salarymanagement.dto.response.SalaryBandStat;
import com.incubyte.salarymanagement.service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/avg-salary-by-department")
    public List<DepartmentSalaryStat> averageSalaryByDepartment() {
        log.info("Fetching average salary by department");
        try {
            return analyticsService.averageSalaryByDepartment();
        } catch (Exception ex) {
            log.error("Failed to fetch average salary by department", ex);
            throw ex;
        }
    }

    @GetMapping("/avg-salary-by-country")
    public List<CountrySalaryStat> averageSalaryByCountry() {
        log.info("Fetching average salary by country");
        try {
            return analyticsService.averageSalaryByCountry();
        } catch (Exception ex) {
            log.error("Failed to fetch average salary by country", ex);
            throw ex;
        }
    }

    @GetMapping("/salary-bands")
    public List<SalaryBandStat> salaryBandDistribution() {
        log.info("Fetching salary band distribution");
        try {
            return analyticsService.salaryBandDistribution();
        } catch (Exception ex) {
            log.error("Failed to fetch salary band distribution", ex);
            throw ex;
        }
    }

    @GetMapping("/headcount-summary")
    public HeadcountSummary headcountSummary() {
        log.info("Fetching headcount summary");
        try {
            return analyticsService.headcountSummary();
        } catch (Exception ex) {
            log.error("Failed to fetch headcount summary", ex);
            throw ex;
        }
    }

    @GetMapping("/payroll-by-currency")
    public List<PayrollByCurrency> payrollByCurrency() {
        log.info("Fetching payroll by currency");
        try {
            return analyticsService.payrollByCurrency();
        } catch (Exception ex) {
            log.error("Failed to fetch payroll by currency", ex);
            throw ex;
        }
    }
}
