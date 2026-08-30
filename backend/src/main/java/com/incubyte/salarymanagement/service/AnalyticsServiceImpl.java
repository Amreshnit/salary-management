package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.dto.CountrySalaryStat;
import com.incubyte.salarymanagement.dto.DepartmentSalaryStat;
import com.incubyte.salarymanagement.dto.SalaryBandStat;
import com.incubyte.salarymanagement.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
