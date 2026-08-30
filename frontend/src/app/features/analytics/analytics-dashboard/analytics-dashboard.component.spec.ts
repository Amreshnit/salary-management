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

  it('groups department stats by currency and tracks the max average per group', () => {
    const component = createComponent([
      { department: 'Engineering', currency: 'USD', averageAmount: 120000, employeeCount: 10 },
      { department: 'Sales', currency: 'USD', averageAmount: 90000, employeeCount: 8 },
      { department: 'Engineering', currency: 'EUR', averageAmount: 100000, employeeCount: 5 },
    ]);

    const groups = component.departmentGroups();

    expect(groups).toHaveLength(2);
    const usdGroup = groups.find((group) => group.currency === 'USD')!;
    expect(usdGroup.rows).toHaveLength(2);
    expect(usdGroup.maxAverageAmount).toBe(120000);
  });

  it('computes bar width as a percentage of the group maximum', () => {
    const component = createComponent([]);

    expect(component.barWidthPercent(50, 100)).toBe(50);
    expect(component.barWidthPercent(0, 0)).toBe(0);
  });

  it('stops loading once all three analytics calls resolve', () => {
    const component = createComponent([]);

    expect(component.loading()).toBe(false);
  });
});
