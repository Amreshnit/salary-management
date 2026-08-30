package com.incubyte.salarymanagement.repository;

import com.incubyte.salarymanagement.model.Employee;
import com.incubyte.salarymanagement.repository.projection.CountrySalaryProjection;
import com.incubyte.salarymanagement.repository.projection.DepartmentSalaryProjection;
import com.incubyte.salarymanagement.repository.projection.HeadcountSummaryProjection;
import com.incubyte.salarymanagement.repository.projection.PayrollByCurrencyProjection;
import com.incubyte.salarymanagement.repository.projection.SalaryBandProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface AnalyticsRepository extends Repository<Employee, Long> {

    @Query(value = """
        SELECT e.department AS department, s.currency AS currency,
               AVG(s.amount) AS averageAmount, COUNT(*) AS employeeCount
        FROM employee e
        JOIN salary_record s ON s.employee_id = e.id AND s.effective_to IS NULL
        WHERE e.status = 'ACTIVE'
        GROUP BY e.department, s.currency
        ORDER BY e.department, s.currency
        """, nativeQuery = true)
    List<DepartmentSalaryProjection> averageSalaryByDepartment();

    @Query(value = """
        SELECT e.country AS country, s.currency AS currency,
               AVG(s.amount) AS averageAmount, COUNT(*) AS employeeCount
        FROM employee e
        JOIN salary_record s ON s.employee_id = e.id AND s.effective_to IS NULL
        WHERE e.status = 'ACTIVE'
        GROUP BY e.country, s.currency
        ORDER BY e.country, s.currency
        """, nativeQuery = true)
    List<CountrySalaryProjection> averageSalaryByCountry();

    @Query(value = """
        SELECT currency AS currency, band AS band,
               MIN(amount) AS minAmount, MAX(amount) AS maxAmount, COUNT(*) AS employeeCount
        FROM (
            SELECT s.currency AS currency, s.amount AS amount,
                   NTILE(5) OVER (PARTITION BY s.currency ORDER BY s.amount) AS band
            FROM salary_record s
            JOIN employee e ON e.id = s.employee_id
            WHERE s.effective_to IS NULL AND e.status = 'ACTIVE'
        ) banded
        GROUP BY currency, band
        ORDER BY currency, band
        """, nativeQuery = true)
    List<SalaryBandProjection> salaryBands();

    @Query(value = """
        SELECT
            COUNT(*) FILTER (WHERE status = 'ACTIVE') AS activeCount,
            COUNT(*) FILTER (WHERE status = 'INACTIVE') AS inactiveCount,
            COUNT(DISTINCT department) FILTER (WHERE status = 'ACTIVE') AS departmentCount,
            COUNT(DISTINCT country) FILTER (WHERE status = 'ACTIVE') AS countryCount,
            COALESCE(AVG((CURRENT_DATE - hire_date) / 365.25) FILTER (WHERE status = 'ACTIVE'), 0) AS avgTenureYears,
            COUNT(*) FILTER (WHERE status = 'ACTIVE' AND hire_date >= CURRENT_DATE - INTERVAL '90 days') AS newHiresLast90Days
        FROM employee
        """, nativeQuery = true)
    HeadcountSummaryProjection headcountSummary();

    @Query(value = """
        SELECT s.currency AS currency, SUM(s.amount) AS totalAnnualCost, COUNT(*) AS employeeCount
        FROM salary_record s
        JOIN employee e ON e.id = s.employee_id
        WHERE s.effective_to IS NULL AND e.status = 'ACTIVE'
        GROUP BY s.currency
        ORDER BY s.currency
        """, nativeQuery = true)
    List<PayrollByCurrencyProjection> payrollByCurrency();
}
