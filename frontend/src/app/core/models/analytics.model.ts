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
