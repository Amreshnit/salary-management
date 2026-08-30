import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonToggleModule } from '@angular/material/button-toggle';

import { AnalyticsService } from '../../../core/services/analytics.service';
import { CountrySalaryStat, DepartmentSalaryStat, SalaryBandStat } from '../../../core/models/analytics.model';
import { colorForBand, colorForCategory } from '../../../shared/chart-colors';

interface CurrencyGroup<T> {
  currency: string;
  rows: T[];
  maxAverageAmount: number;
}

@Component({
  selector: 'app-analytics-dashboard',
  imports: [
    DecimalPipe,
    MatCardModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatButtonToggleModule,
  ],
  templateUrl: './analytics-dashboard.component.html',
  styleUrl: './analytics-dashboard.component.scss',
})
export class AnalyticsDashboardComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly loading = signal(true);
  readonly departmentStats = signal<DepartmentSalaryStat[]>([]);
  readonly countryStats = signal<CountrySalaryStat[]>([]);
  readonly bandStats = signal<SalaryBandStat[]>([]);
  readonly selectedCurrency = signal<string | null>(null);
  readonly salaryBandColumns = ['band', 'range', 'employeeCount'];

  readonly availableCurrencies = computed(() => {
    const currencies = new Set(this.departmentStats().map((row) => row.currency));
    return Array.from(currencies).sort();
  });

  private readonly departmentGroupsByCurrency = computed(() =>
    this.groupByCurrency(this.departmentStats(), (row) => row.averageAmount),
  );
  private readonly countryGroupsByCurrency = computed(() =>
    this.groupByCurrency(this.countryStats(), (row) => row.averageAmount),
  );
  private readonly bandGroupsByCurrency = computed(() => this.groupBandsByCurrency(this.bandStats()));

  readonly currentDepartmentGroup = computed(() =>
    this.departmentGroupsByCurrency().find((group) => group.currency === this.selectedCurrency()),
  );
  readonly currentCountryGroup = computed(() =>
    this.countryGroupsByCurrency().find((group) => group.currency === this.selectedCurrency()),
  );
  readonly currentBandGroup = computed(() =>
    this.bandGroupsByCurrency().find((group) => group.currency === this.selectedCurrency()),
  );

  ngOnInit(): void {
    this.analyticsService.averageSalaryByDepartment().subscribe((stats) => {
      this.departmentStats.set(stats);
      if (!this.selectedCurrency() && stats.length > 0) {
        this.selectedCurrency.set(this.availableCurrencies()[0]);
      }
    });
    this.analyticsService.averageSalaryByCountry().subscribe((stats) => this.countryStats.set(stats));
    this.analyticsService.salaryBandDistribution().subscribe((stats) => {
      this.bandStats.set(stats);
      this.loading.set(false);
    });
  }

  selectCurrency(currency: string): void {
    this.selectedCurrency.set(currency);
  }

  barWidthPercent(value: number, max: number): number {
    return max === 0 ? 0 : Math.round((value / max) * 100);
  }

  colorForCategory(categoryName: string): string {
    return colorForCategory(categoryName);
  }

  colorForBand(band: number, totalBands: number): string {
    return colorForBand(band, totalBands);
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
