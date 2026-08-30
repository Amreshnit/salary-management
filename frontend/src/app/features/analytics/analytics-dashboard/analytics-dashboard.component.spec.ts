import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AnalyticsDashboardComponent } from './analytics-dashboard.component';
import { AnalyticsService } from '../../../core/services/analytics.service';
import { DepartmentSalaryStat } from '../../../core/models/analytics.model';

describe('AnalyticsDashboardComponent', () => {
  function createComponent(departmentStats: DepartmentSalaryStat[]) {
    TestBed.configureTestingModule({
      imports: [AnalyticsDashboardComponent],
      providers: [
        {
          provide: AnalyticsService,
          useValue: {
            averageSalaryByDepartment: () => of(departmentStats),
            averageSalaryByCountry: () => of([]),
            salaryBandDistribution: () => of([]),
          },
        },
      ],
    });
    const fixture = TestBed.createComponent(AnalyticsDashboardComponent);
    fixture.detectChanges();
    return fixture.componentInstance;
  }

  it('lists the distinct currencies present in the department stats and selects the first one', () => {
    const component = createComponent([
      { department: 'Engineering', currency: 'USD', averageAmount: 120000, employeeCount: 10 },
      { department: 'Engineering', currency: 'EUR', averageAmount: 100000, employeeCount: 5 },
    ]);

    expect(component.availableCurrencies()).toEqual(['EUR', 'USD']);
    expect(component.selectedCurrency()).toBe('EUR');
  });

  it('groups department stats for the selected currency and tracks the max average', () => {
    const component = createComponent([
      { department: 'Engineering', currency: 'USD', averageAmount: 120000, employeeCount: 10 },
      { department: 'Sales', currency: 'USD', averageAmount: 90000, employeeCount: 8 },
      { department: 'Engineering', currency: 'EUR', averageAmount: 100000, employeeCount: 5 },
    ]);

    component.selectCurrency('USD');

    const group = component.currentDepartmentGroup();
    expect(group?.rows).toHaveLength(2);
    expect(group?.maxAverageAmount).toBe(120000);
  });

  it('computes bar width as a percentage of the group maximum', () => {
    const component = createComponent([]);

    expect(component.barWidthPercent(50, 100)).toBe(50);
    expect(component.barWidthPercent(0, 0)).toBe(0);
  });

  it('assigns a consistent color to the same category name', () => {
    const component = createComponent([]);

    expect(component.colorForCategory('Engineering')).toBe(component.colorForCategory('Engineering'));
  });

  it('stops loading once all three analytics calls resolve', () => {
    const component = createComponent([]);

    expect(component.loading()).toBe(false);
  });
});
