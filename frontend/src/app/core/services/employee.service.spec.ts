import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { EmployeeService } from './employee.service';
import { Employee } from '../models/employee.model';

describe('EmployeeService', () => {
  let service: EmployeeService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EmployeeService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(EmployeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('sends department, status, and search filters as query parameters', () => {
    service
      .searchEmployees({ department: 'Engineering', status: 'ACTIVE', q: 'ada', page: 1, size: 10 })
      .subscribe();

    const request = httpMock.expectOne(
      (req) =>
        req.url === '/api/v1/employees' &&
        req.params.get('department') === 'Engineering' &&
        req.params.get('status') === 'ACTIVE' &&
        req.params.get('q') === 'ada' &&
        req.params.get('page') === '1' &&
        req.params.get('size') === '10',
    );
    expect(request.request.method).toBe('GET');
    request.flush({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 });
  });

  it('defaults to page 0 and size 20 when no paging is requested', () => {
    service.searchEmployees({}).subscribe();

    const request = httpMock.expectOne(
      (req) => req.url === '/api/v1/employees' && req.params.get('page') === '0' && req.params.get('size') === '20',
    );
    request.flush({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 20 });
  });

  it('posts a create request to the employees endpoint', () => {
    const request$ = service.createEmployee({
      firstName: 'Ada',
      lastName: 'Lovelace',
      email: 'ada@acme-corp.example',
      department: 'Engineering',
      jobTitle: 'Senior Software Engineer',
      seniorityLevel: 'Senior',
      country: 'United States',
      currency: 'USD',
      hireDate: '2024-01-01',
      startingSalary: 120000,
    });
    request$.subscribe();

    const request = httpMock.expectOne('/api/v1/employees');
    expect(request.request.method).toBe('POST');
    expect(request.request.body.email).toBe('ada@acme-corp.example');
    request.flush({} as Employee);
  });

  it('sends a delete request to deactivate an employee', () => {
    service.deactivateEmployee(42).subscribe();

    const request = httpMock.expectOne('/api/v1/employees/42');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });

  it('posts a new salary record to the employee salary-records endpoint', () => {
    service
      .addSalaryRecord(7, { amount: 100000, currency: 'USD', effectiveFrom: '2025-01-01', reason: 'RAISE' })
      .subscribe();

    const request = httpMock.expectOne('/api/v1/employees/7/salary-records');
    expect(request.request.method).toBe('POST');
    expect(request.request.body.reason).toBe('RAISE');
    request.flush({});
  });
});
