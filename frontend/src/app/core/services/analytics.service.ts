import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { CountrySalaryStat, DepartmentSalaryStat, SalaryBandStat } from '../models/analytics.model';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/analytics';

  averageSalaryByDepartment(): Observable<DepartmentSalaryStat[]> {
    return this.http.get<DepartmentSalaryStat[]>(`${this.baseUrl}/avg-salary-by-department`);
  }

  averageSalaryByCountry(): Observable<CountrySalaryStat[]> {
    return this.http.get<CountrySalaryStat[]>(`${this.baseUrl}/avg-salary-by-country`);
  }

  salaryBandDistribution(): Observable<SalaryBandStat[]> {
    return this.http.get<SalaryBandStat[]>(`${this.baseUrl}/salary-bands`);
  }
}
