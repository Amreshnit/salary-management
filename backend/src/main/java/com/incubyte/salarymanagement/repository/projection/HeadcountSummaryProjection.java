package com.incubyte.salarymanagement.repository.projection;

import java.math.BigDecimal;

public interface HeadcountSummaryProjection {
    Long getActiveCount();
    Long getInactiveCount();
    Long getDepartmentCount();
    Long getCountryCount();
    BigDecimal getAvgTenureYears();
    Long getNewHiresLast90Days();
}
