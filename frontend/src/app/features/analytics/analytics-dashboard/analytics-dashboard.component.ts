import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';

import { AnalyticsService } from '../../../core/services/analytics.service';
import { CountrySalaryStat, DepartmentSalaryStat, SalaryBandStat } from '../../../core/models/analytics.model';

interface CurrencyGroup<T> {
  currency: string;
  rows: T[];
  maxAverageAmount: number;
}

@Component({
  selector: 'app-analytics-dashboard',
  imports: [DecimalPipe, MatCardModule, MatTableModule, MatProgressSpinnerModule, MatTabsModule],
  templateUrl: './analytics-dashboard.component.html',
  styleUrl: './analytics-dashboard.component.scss',
})
export class AnalyticsDashboardComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly loading = signal(true);
  readonly departmentStats = signal<DepartmentSalaryStat[]>([]);
  readonly countryStats = signal<CountrySalaryStat[]>([]);
  readonly bandStats = signal<SalaryBandStat[]>([]);
  readonly salaryBandColumns = ['band', 'range', 'employeeCount'];

  readonly departmentGroups = computed(() => this.groupByCurrency(this.departmentStats(), (row) => row.averageAmount));
  readonly countryGroups = computed(() => this.groupByCurrency(this.countryStats(), (row) => row.averageAmount));
  readonly bandGroups = computed(() => this.groupBandsByCurrency(this.bandStats()));

  ngOnInit(): void {
    this.analyticsService.averageSalaryByDepartment().subscribe((stats) => this.departmentStats.set(stats));
    this.analyticsService.averageSalaryByCountry().subscribe((stats) => this.countryStats.set(stats));
    this.analyticsService.salaryBandDistribution().subscribe((stats) => {
      this.bandStats.set(stats);
      this.loading.set(false);
    });
  }

  barWidthPercent(value: number, max: number): number {
    return max === 0 ? 0 : Math.round((value / max) * 100);
  }

  private groupByCurrency<T extends { currency: string }>(
    rows: T[],
    valueSelector: (row: T) => number,
  ): CurrencyGroup<T>[] {
    const byCurrency = new Map<string, T[]>();
    for (const row of rows) {
      const existing = byCurrency.get(row.currency) ?? [];
      existing.push(row);
      byCurrency.set(row.currency, existing);
    }
    return Array.from(byCurrency.entries())
      .map(([currency, currencyRows]) => ({
        currency,
        rows: currencyRows,
        maxAverageAmount: Math.max(...currencyRows.map(valueSelector)),
      }))
      .sort((a, b) => a.currency.localeCompare(b.currency));
  }

  private groupBandsByCurrency(rows: SalaryBandStat[]): { currency: string; rows: SalaryBandStat[] }[] {
    const byCurrency = new Map<string, SalaryBandStat[]>();
    for (const row of rows) {
      const existing = byCurrency.get(row.currency) ?? [];
      existing.push(row);
      byCurrency.set(row.currency, existing);
    }
    return Array.from(byCurrency.entries())
      .map(([currency, currencyRows]) => ({ currency, rows: currencyRows.sort((a, b) => a.band - b.band) }))
      .sort((a, b) => a.currency.localeCompare(b.currency));
  }
}
