import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Employee,
  EmployeeCreateRequest,
  EmployeeSearchParams,
  EmployeeUpdateRequest,
} from '../models/employee.model';
import { Page } from '../models/page.model';
import { SalaryRecord, SalaryRecordRequest } from '../models/salary-record.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/employees';

  searchEmployees(params: EmployeeSearchParams): Observable<Page<Employee>> {
    let httpParams = new HttpParams();
    if (params.department) {
      httpParams = httpParams.set('department', params.department);
    }
    if (params.country) {
      httpParams = httpParams.set('country', params.country);
    }
    if (params.status) {
      httpParams = httpParams.set('status', params.status);
    }
    if (params.q) {
      httpParams = httpParams.set('q', params.q);
    }
    httpParams = httpParams.set('page', params.page ?? 0).set('size', params.size ?? 20);

    return this.http.get<Page<Employee>>(this.baseUrl, { params: httpParams });
  }

  getEmployeeById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/${id}`);
  }

  createEmployee(request: EmployeeCreateRequest): Observable<Employee> {
    return this.http.post<Employee>(this.baseUrl, request);
  }

  updateEmployee(id: number, request: EmployeeUpdateRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.baseUrl}/${id}`, request);
  }

  deactivateEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getSalaryHistory(id: number): Observable<SalaryRecord[]> {
    return this.http.get<SalaryRecord[]>(`${this.baseUrl}/${id}/salary-history`);
  }

  addSalaryRecord(id: number, request: SalaryRecordRequest): Observable<SalaryRecord> {
    return this.http.post<SalaryRecord>(`${this.baseUrl}/${id}/salary-records`, request);
  }
}
