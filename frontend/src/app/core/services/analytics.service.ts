import { Observable } from 'rxjs';

import {
  CountrySalaryStat,
  DepartmentSalaryStat,
  HeadcountSummary,
  PayrollByCurrency,
  SalaryBandStat,
} from '../models/analytics.model';

export abstract class AnalyticsService {
  abstract averageSalaryByDepartment(): Observable<DepartmentSalaryStat[]>;

  abstract averageSalaryByCountry(): Observable<CountrySalaryStat[]>;

  abstract salaryBandDistribution(): Observable<SalaryBandStat[]>;

  abstract headcountSummary(): Observable<HeadcountSummary>;

  abstract payrollByCurrency(): Observable<PayrollByCurrency[]>;
}
