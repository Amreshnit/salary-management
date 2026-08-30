export interface DepartmentSalaryStat {
  department: string;
  currency: string;
  averageAmount: number;
  employeeCount: number;
}

export interface CountrySalaryStat {
  country: string;
  currency: string;
  averageAmount: number;
  employeeCount: number;
}

export interface SalaryBandStat {
  currency: string;
  band: number;
  minAmount: number;
  maxAmount: number;
  employeeCount: number;
}

export interface HeadcountSummary {
  activeEmployees: number;
  inactiveEmployees: number;
  departments: number;
  countries: number;
  averageTenureYears: number;
  newHiresLast90Days: number;
}

export interface PayrollByCurrency {
  currency: string;
  totalAnnualCost: number;
  employeeCount: number;
}
