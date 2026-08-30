import { Observable } from 'rxjs';

import { CountrySalaryStat, DepartmentSalaryStat, SalaryBandStat } from '../models/analytics.model';

export abstract class AnalyticsService {
  abstract averageSalaryByDepartment(): Observable<DepartmentSalaryStat[]>;

  abstract averageSalaryByCountry(): Observable<CountrySalaryStat[]>;

  abstract salaryBandDistribution(): Observable<SalaryBandStat[]>;
}
