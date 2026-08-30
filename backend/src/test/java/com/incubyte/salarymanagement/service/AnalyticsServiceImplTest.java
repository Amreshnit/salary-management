package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.dto.DepartmentSalaryStat;
import com.incubyte.salarymanagement.repository.AnalyticsRepository;
import com.incubyte.salarymanagement.repository.projection.DepartmentSalaryProjection;
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
}
