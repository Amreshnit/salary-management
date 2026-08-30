package com.incubyte.salarymanagement.repository.projection;

import java.math.BigDecimal;

public interface DepartmentSalaryProjection {
    String getDepartment();
    String getCurrency();
    BigDecimal getAverageAmount();
    Long getEmployeeCount();
}
