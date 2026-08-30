import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { EmployeeFormComponent } from './employee-form.component';
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

describe('EmployeeFormComponent', () => {
  function createComponent(employeeServiceStub: Partial<EmployeeService>, routeId: string | null = null) {
    TestBed.configureTestingModule({
      imports: [EmployeeFormComponent],
      providers: [
        provideRouter([]),
        { provide: EmployeeService, useValue: employeeServiceStub },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap(routeId ? { id: routeId } : {}) } },
        },
      ],
    });
    const fixture = TestBed.createComponent(EmployeeFormComponent);
    fixture.detectChanges();
    return { fixture, component: fixture.componentInstance };
  }

  function fillRequiredFields(component: EmployeeFormComponent) {
    component.form.setValue({
      firstName: 'Grace',
      lastName: 'Hopper',
      email: 'grace.hopper@acme-corp.example',
      department: 'Engineering',
      jobTitle: 'Principal Engineer',
      seniorityLevel: 'Senior',
      countryIsoCode: 'US',
      stateIsoCode: '',
      address: '',
      hireDate: new Date('2024-01-01'),
      startingSalary: 150000,
    });
  }

  it('navigates to the new employee on successful create', () => {
    const createdEmployee = buildEmployee({ id: 42 });
    const createEmployee = vi.fn().mockReturnValue(of(createdEmployee));
    const { component } = createComponent({ createEmployee });
    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigate');

    fillRequiredFields(component);
    component.submit();

    expect(createEmployee).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/employees', 42]);
  });

  it('resets the saving flag so the form can be resubmitted after a failed create', () => {
    const createEmployee = vi.fn().mockReturnValue(throwError(() => new Error('Conflict')));
    const { component } = createComponent({ createEmployee });

    fillRequiredFields(component);
    component.submit();

    expect(component.saving()).toBe(false);
  });

  it('resets the saving flag so the form can be resubmitted after a failed update', () => {
    const getEmployeeById = vi.fn().mockReturnValue(of(buildEmployee()));
    const updateEmployee = vi.fn().mockReturnValue(throwError(() => new Error('Conflict')));
    const { component } = createComponent({ getEmployeeById, updateEmployee }, '1');

    fillRequiredFields(component);
    component.submit();

    expect(component.saving()).toBe(false);
  });
});
