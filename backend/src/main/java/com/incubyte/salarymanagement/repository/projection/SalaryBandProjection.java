package com.incubyte.salarymanagement.repository.projection;

import java.math.BigDecimal;

public interface SalaryBandProjection {
    String getCurrency();
    Integer getBand();
    BigDecimal getMinAmount();
    BigDecimal getMaxAmount();
    Long getEmployeeCount();
}
