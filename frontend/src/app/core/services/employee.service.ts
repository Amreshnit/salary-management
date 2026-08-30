import { Observable } from 'rxjs';

import { Employee, EmployeeCreateRequest, EmployeeSearchParams, EmployeeUpdateRequest } from '../models/employee.model';
import { Page } from '../models/page.model';
import { SalaryRecord, SalaryRecordRequest } from '../models/salary-record.model';

export abstract class EmployeeService {
  abstract searchEmployees(params: EmployeeSearchParams): Observable<Page<Employee>>;

  abstract getEmployeeById(id: number): Observable<Employee>;

  abstract createEmployee(request: EmployeeCreateRequest): Observable<Employee>;

  abstract updateEmployee(id: number, request: EmployeeUpdateRequest): Observable<Employee>;

  abstract deactivateEmployee(id: number): Observable<void>;

  abstract activateEmployee(id: number): Observable<void>;

  abstract deleteEmployee(id: number): Observable<void>;

  abstract getSalaryHistory(id: number): Observable<SalaryRecord[]>;

  abstract addSalaryRecord(id: number, request: SalaryRecordRequest): Observable<SalaryRecord>;

  abstract getDistinctDepartments(): Observable<string[]>;

  abstract getDistinctCountries(): Observable<string[]>;
}
