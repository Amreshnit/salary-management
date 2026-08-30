package com.incubyte.salarymanagement.web;

import com.incubyte.salarymanagement.dto.CountrySalaryStat;
import com.incubyte.salarymanagement.dto.DepartmentSalaryStat;
import com.incubyte.salarymanagement.dto.HeadcountSummary;
import com.incubyte.salarymanagement.dto.PayrollByCurrency;
import com.incubyte.salarymanagement.dto.SalaryBandStat;
import com.incubyte.salarymanagement.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/avg-salary-by-department")
    public List<DepartmentSalaryStat> averageSalaryByDepartment() {
        return analyticsService.averageSalaryByDepartment();
    }

    @GetMapping("/avg-salary-by-country")
    public List<CountrySalaryStat> averageSalaryByCountry() {
        return analyticsService.averageSalaryByCountry();
    }

    @GetMapping("/salary-bands")
    public List<SalaryBandStat> salaryBandDistribution() {
        return analyticsService.salaryBandDistribution();
    }

    @GetMapping("/headcount-summary")
    public HeadcountSummary headcountSummary() {
        return analyticsService.headcountSummary();
    }

    @GetMapping("/payroll-by-currency")
    public List<PayrollByCurrency> payrollByCurrency() {
        return analyticsService.payrollByCurrency();
    }
}
