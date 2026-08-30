package com.incubyte.salarymanagement.repository.projection;

import java.math.BigDecimal;

public interface CountrySalaryProjection {
    String getCountry();
    String getCurrency();
    BigDecimal getAverageAmount();
    Long getEmployeeCount();
}
