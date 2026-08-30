package com.incubyte.salarymanagement.repository.projection;

import java.math.BigDecimal;

public interface PayrollByCurrencyProjection {
    String getCurrency();
    BigDecimal getTotalAnnualCost();
    Long getEmployeeCount();
}
