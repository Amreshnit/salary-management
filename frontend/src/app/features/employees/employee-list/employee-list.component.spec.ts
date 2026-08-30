import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { Mock, vi } from 'vitest';

import { EmployeeListComponent } from './employee-list.component';
import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';

function buildEmployee(overrides: Partial<Employee> = {}): Employee {
  return {
    id: 1,
    employeeCode: 'EMP-00001',
    firstName: 'Ada',
    lastName: 'Lovelace',
    email: 'ada@acme-corp.example',
    department: 'Engineering',
    jobTitle: 'Senior Software Engineer',
    seniorityLevel: 'Senior',
    country: 'United States',
    state: null,
    address: null,
    currency: 'USD',
    hireDate: '2024-01-01',
    status: 'ACTIVE',
    currentSalaryAmount: 120000,
    currentSalaryCurrency: 'USD',
    ...overrides,
  };
}

describe('EmployeeListComponent', () => {
  let searchEmployeesSpy: Mock;

  beforeEach(() => {
    searchEmployeesSpy = vi.fn().mockReturnValue(
      of({
        content: [buildEmployee()],
        totalElements: 1,
        totalPages: 1,
        number: 0,
        size: 20,
      }),
    );

    TestBed.configureTestingModule({
      imports: [EmployeeListComponent],
      providers: [provideRouter([]), { provide: EmployeeService, useValue: { searchEmployees: searchEmployeesSpy } }],
    });
  });

  it('loads employees into the table on init', () => {
    const fixture = TestBed.createComponent(EmployeeListComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    expect(component.employees()).toHaveLength(1);
    expect(component.employees()[0].employeeCode).toBe('EMP-00001');
    expect(component.totalElements()).toBe(1);
  });

  it('resets to the first page and re-queries when filters change', () => {
    const fixture = TestBed.createComponent(EmployeeListComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.pageIndex = 3;
    component.selectedDepartment = 'Engineering';
    component.onFiltersChanged();

    expect(component.pageIndex).toBe(0);
    expect(searchEmployeesSpy).toHaveBeenCalledWith(
      expect.objectContaining({ department: 'Engineering', page: 0 }),
    );
  });

  it('re-queries with the new page and size when the paginator changes', () => {
    const fixture = TestBed.createComponent(EmployeeListComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.onPageChanged({ pageIndex: 2, pageSize: 50, length: 1 });

    expect(component.pageIndex).toBe(2);
    expect(component.pageSize).toBe(50);
    expect(searchEmployeesSpy).toHaveBeenCalledWith(expect.objectContaining({ page: 2, size: 50 }));
  });
});
