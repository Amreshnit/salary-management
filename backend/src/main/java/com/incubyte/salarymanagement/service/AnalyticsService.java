package com.incubyte.salarymanagement.service;

import com.incubyte.salarymanagement.dto.response.CountrySalaryStat;
import com.incubyte.salarymanagement.dto.response.DepartmentSalaryStat;
import com.incubyte.salarymanagement.dto.response.HeadcountSummary;
import com.incubyte.salarymanagement.dto.response.PayrollByCurrency;
import com.incubyte.salarymanagement.dto.response.SalaryBandStat;

import java.util.List;

public interface AnalyticsService {

    List<DepartmentSalaryStat> averageSalaryByDepartment();

    List<CountrySalaryStat> averageSalaryByCountry();

    List<SalaryBandStat> salaryBandDistribution();

    HeadcountSummary headcountSummary();

    List<PayrollByCurrency> payrollByCurrency();
}
