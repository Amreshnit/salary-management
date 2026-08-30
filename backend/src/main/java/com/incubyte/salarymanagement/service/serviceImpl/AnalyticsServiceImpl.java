package com.incubyte.salarymanagement.service.serviceImpl;

import com.incubyte.salarymanagement.dto.response.CountrySalaryStat;
import com.incubyte.salarymanagement.dto.response.DepartmentSalaryStat;
import com.incubyte.salarymanagement.dto.response.HeadcountSummary;
import com.incubyte.salarymanagement.dto.response.PayrollByCurrency;
import com.incubyte.salarymanagement.dto.response.SalaryBandStat;
import com.incubyte.salarymanagement.repository.AnalyticsRepository;
import com.incubyte.salarymanagement.repository.projection.HeadcountSummaryProjection;
import com.incubyte.salarymanagement.service.AnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsServiceImpl(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentSalaryStat> averageSalaryByDepartment() {
        return analyticsRepository.averageSalaryByDepartment().stream()
                .map(row -> new DepartmentSalaryStat(row.getDepartment(), row.getCurrency(), row.getAverageAmount(), row.getEmployeeCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountrySalaryStat> averageSalaryByCountry() {
        return analyticsRepository.averageSalaryByCountry().stream()
                .map(row -> new CountrySalaryStat(row.getCountry(), row.getCurrency(), row.getAverageAmount(), row.getEmployeeCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryBandStat> salaryBandDistribution() {
        return analyticsRepository.salaryBands().stream()
                .map(row -> new SalaryBandStat(row.getCurrency(), row.getBand(), row.getMinAmount(), row.getMaxAmount(), row.getEmployeeCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HeadcountSummary headcountSummary() {
        HeadcountSummaryProjection row = analyticsRepository.headcountSummary();
        BigDecimal averageTenureYears = row.getAvgTenureYears() != null
                ? row.getAvgTenureYears().setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return new HeadcountSummary(
                row.getActiveCount(),
                row.getInactiveCount(),
                row.getDepartmentCount(),
                row.getCountryCount(),
                averageTenureYears,
                row.getNewHiresLast90Days()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollByCurrency> payrollByCurrency() {
        return analyticsRepository.payrollByCurrency().stream()
                .map(row -> new PayrollByCurrency(row.getCurrency(), row.getTotalAnnualCost(), row.getEmployeeCount()))
                .toList();
    }
}
