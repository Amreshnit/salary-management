import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';

import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';
import { SalaryRecord } from '../../../core/models/salary-record.model';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-employee-detail',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './employee-detail.component.html',
  styleUrl: './employee-detail.component.scss',
})
export class EmployeeDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly employeeService = inject(EmployeeService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly dialog = inject(MatDialog);

  readonly employee = signal<Employee | null>(null);
  readonly salaryHistory = signal<SalaryRecord[]>([]);
  readonly loading = signal(true);
  readonly showSalaryForm = signal(false);
  readonly salaryHistoryColumns = ['effectiveFrom', 'effectiveTo', 'amount', 'reason'];

  readonly salaryForm = this.formBuilder.nonNullable.group({
    amount: [0, [Validators.required, Validators.min(1)]],
    effectiveFrom: [new Date(), Validators.required],
    reason: ['RAISE', Validators.required],
  });

  private employeeId(): number {
    return Number(this.route.snapshot.paramMap.get('id'));
  }

  ngOnInit(): void {
    this.loadEmployee();
  }

  loadEmployee(): void {
    this.loading.set(true);
    const id = this.employeeId();
    this.employeeService.getEmployeeById(id).subscribe((employee) => {
      this.employee.set(employee);
      this.loading.set(false);
    });
    this.employeeService.getSalaryHistory(id).subscribe((history) => this.salaryHistory.set(history));
  }

  toggleSalaryForm(): void {
    this.showSalaryForm.set(!this.showSalaryForm());
  }

  submitSalaryChange(): void {
    if (this.salaryForm.invalid) {
      return;
    }
    const value = this.salaryForm.getRawValue();
    const employee = this.employee();
    if (!employee) {
      return;
    }
    this.employeeService
      .addSalaryRecord(employee.id, {
        amount: value.amount,
        currency: employee.currency,
        effectiveFrom: this.toIsoDate(value.effectiveFrom),
        reason: value.reason as SalaryRecord['reason'],
      })
      .subscribe(() => {
        this.showSalaryForm.set(false);
        this.salaryForm.reset({ amount: 0, effectiveFrom: new Date(), reason: 'RAISE' });
        this.loadEmployee();
      });
  }

  deactivateEmployee(): void {
    const employee = this.employee();
    if (!employee) {
      return;
    }
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Deactivate employee',
        message: `Deactivate ${employee.firstName} ${employee.lastName}? This can be reversed later by editing their record.`,
      },
    });
    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.employeeService.deactivateEmployee(employee.id).subscribe(() => this.loadEmployee());
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/employees']);
  }

  private toIsoDate(date: Date): string {
    return date.toISOString().slice(0, 10);
  }
}
