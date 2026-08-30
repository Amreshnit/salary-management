package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.dto.CountrySalaryStat;
import com.incubyte.salarymanagement.dto.DepartmentSalaryStat;
import com.incubyte.salarymanagement.dto.SalaryBandStat;

import java.util.List;

public interface AnalyticsService {

    List<DepartmentSalaryStat> averageSalaryByDepartment();

    List<CountrySalaryStat> averageSalaryByCountry();

    List<SalaryBandStat> salaryBandDistribution();
}
