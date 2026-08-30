import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { AnalyticsService } from './analytics.service';
import { CountrySalaryStat, DepartmentSalaryStat, SalaryBandStat } from '../models/analytics.model';

@Injectable()
export class AnalyticsServiceImpl extends AnalyticsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/analytics';

  override averageSalaryByDepartment(): Observable<DepartmentSalaryStat[]> {
    return this.http.get<DepartmentSalaryStat[]>(`${this.baseUrl}/avg-salary-by-department`);
  }

  override averageSalaryByCountry(): Observable<CountrySalaryStat[]> {
    return this.http.get<CountrySalaryStat[]>(`${this.baseUrl}/avg-salary-by-country`);
  }

  override salaryBandDistribution(): Observable<SalaryBandStat[]> {
    return this.http.get<SalaryBandStat[]>(`${this.baseUrl}/salary-bands`);
  }
}
