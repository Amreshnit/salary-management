import { Component, OnInit, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';

import { EmployeeService } from '../../../core/services/employee.service';
import { Employee, EmployeeStatus } from '../../../core/models/employee.model';
import { COUNTRIES, DEPARTMENTS } from '../../../core/models/reference-data';

@Component({
  selector: 'app-employee-list',
  imports: [
    DecimalPipe,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
  ],
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.scss',
})
export class EmployeeListComponent implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);

  readonly displayedColumns = ['employeeCode', 'name', 'department', 'country', 'jobTitle', 'salary', 'status'];
  readonly departments = DEPARTMENTS;
  readonly countries = COUNTRIES;

  readonly employees = signal<Employee[]>([]);
  readonly totalElements = signal(0);
  readonly loading = signal(false);

  searchTerm = '';
  selectedDepartment: string | null = null;
  selectedCountry: string | null = null;
  selectedStatus: EmployeeStatus | null = null;
  pageIndex = 0;
  pageSize = 20;

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.loading.set(true);
    this.employeeService
      .searchEmployees({
        department: this.selectedDepartment ?? undefined,
        country: this.selectedCountry ?? undefined,
        status: this.selectedStatus ?? undefined,
        q: this.searchTerm || undefined,
        page: this.pageIndex,
        size: this.pageSize,
      })
      .subscribe({
        next: (page) => {
          this.employees.set(page.content);
          this.totalElements.set(page.totalElements);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  onFiltersChanged(): void {
    this.pageIndex = 0;
    this.loadEmployees();
  }

  onPageChanged(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadEmployees();
  }

  openEmployee(employee: Employee): void {
    this.router.navigate(['/employees', employee.id]);
  }
}
