package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.dto.DepartmentSalaryStat;
import com.incubyte.salarymanagement.dto.HeadcountSummary;
import com.incubyte.salarymanagement.dto.PayrollByCurrency;
import com.incubyte.salarymanagement.repository.AnalyticsRepository;
import com.incubyte.salarymanagement.repository.projection.DepartmentSalaryProjection;
import com.incubyte.salarymanagement.repository.projection.HeadcountSummaryProjection;
import com.incubyte.salarymanagement.repository.projection.PayrollByCurrencyProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    private AnalyticsServiceImpl analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsServiceImpl(analyticsRepository);
    }

    @Test
    void averageSalaryByDepartmentMapsProjectionRowsToResponseDtos() {
        DepartmentSalaryProjection row = new DepartmentSalaryProjection() {
            @Override public String getDepartment() { return "Engineering"; }
            @Override public String getCurrency() { return "USD"; }
            @Override public BigDecimal getAverageAmount() { return new BigDecimal("105000.00"); }
            @Override public Long getEmployeeCount() { return 42L; }
        };
        when(analyticsRepository.averageSalaryByDepartment()).thenReturn(List.of(row));

        List<DepartmentSalaryStat> result = analyticsService.averageSalaryByDepartment();

        assertThat(result).hasSize(1);
        DepartmentSalaryStat stat = result.get(0);
        assertThat(stat.department()).isEqualTo("Engineering");
        assertThat(stat.currency()).isEqualTo("USD");
        assertThat(stat.averageAmount()).isEqualByComparingTo("105000.00");
        assertThat(stat.employeeCount()).isEqualTo(42L);
    }

    @Test
    void headcountSummaryRoundsAverageTenureAndMapsAllCounts() {
        HeadcountSummaryProjection row = new HeadcountSummaryProjection() {
            @Override public Long getActiveCount() { return 9800L; }
            @Override public Long getInactiveCount() { return 200L; }
            @Override public Long getDepartmentCount() { return 10L; }
            @Override public Long getCountryCount() { return 9L; }
            @Override public BigDecimal getAvgTenureYears() { return new BigDecimal("2.3456"); }
            @Override public Long getNewHiresLast90Days() { return 150L; }
        };
        when(analyticsRepository.headcountSummary()).thenReturn(row);

        HeadcountSummary summary = analyticsService.headcountSummary();

        assertThat(summary.activeEmployees()).isEqualTo(9800L);
        assertThat(summary.inactiveEmployees()).isEqualTo(200L);
        assertThat(summary.departments()).isEqualTo(10L);
        assertThat(summary.countries()).isEqualTo(9L);
        assertThat(summary.averageTenureYears()).isEqualByComparingTo("2.3");
        assertThat(summary.newHiresLast90Days()).isEqualTo(150L);
    }

    @Test
    void payrollByCurrencyMapsProjectionRowsToResponseDtos() {
        PayrollByCurrencyProjection row = new PayrollByCurrencyProjection() {
            @Override public String getCurrency() { return "USD"; }
            @Override public BigDecimal getTotalAnnualCost() { return new BigDecimal("50000000.00"); }
            @Override public Long getEmployeeCount() { return 500L; }
        };
        when(analyticsRepository.payrollByCurrency()).thenReturn(List.of(row));

        List<PayrollByCurrency> result = analyticsService.payrollByCurrency();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).currency()).isEqualTo("USD");
        assertThat(result.get(0).totalAnnualCost()).isEqualByComparingTo("50000000.00");
        assertThat(result.get(0).employeeCount()).isEqualTo(500L);
    }
}
