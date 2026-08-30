import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { EmployeeService } from '../../../core/services/employee.service';
import { COUNTRIES, DEPARTMENTS, SENIORITY_LEVELS } from '../../../core/models/reference-data';

@Component({
  selector: 'app-employee-form',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './employee-form.component.html',
  styleUrl: './employee-form.component.scss',
})
export class EmployeeFormComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly employeeService = inject(EmployeeService);
  private readonly formBuilder = inject(FormBuilder);

  readonly departments = DEPARTMENTS;
  readonly seniorityLevels = SENIORITY_LEVELS;
  readonly countries = COUNTRIES;

  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly employeeId = signal<number | null>(null);
  readonly isEditMode = computed(() => this.employeeId() !== null);

  readonly form = this.formBuilder.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    department: ['', Validators.required],
    jobTitle: ['', Validators.required],
    seniorityLevel: ['', Validators.required],
    country: ['', Validators.required],
    hireDate: [new Date(), Validators.required],
    startingSalary: [0, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.employeeId.set(id);
      this.loading.set(true);
      this.employeeService.getEmployeeById(id).subscribe((employee) => {
        this.form.patchValue({
          firstName: employee.firstName,
          lastName: employee.lastName,
          email: employee.email,
          department: employee.department,
          jobTitle: employee.jobTitle,
          seniorityLevel: employee.seniorityLevel,
          country: employee.country,
        });
        this.loading.set(false);
      });
    }
  }

  currencyForSelectedCountry(): string {
    const countryName = this.form.controls.country.value;
    return this.countries.find((country) => country.country === countryName)?.currency ?? '';
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving.set(true);
    const value = this.form.getRawValue();
    const currency = this.currencyForSelectedCountry();
    const id = this.employeeId();

    if (id !== null) {
      this.employeeService
        .updateEmployee(id, {
          firstName: value.firstName,
          lastName: value.lastName,
          email: value.email,
          department: value.department,
          jobTitle: value.jobTitle,
          seniorityLevel: value.seniorityLevel,
          country: value.country,
          currency,
        })
        .subscribe(() => this.router.navigate(['/employees', id]));
      return;
    }

    this.employeeService
      .createEmployee({
        firstName: value.firstName,
        lastName: value.lastName,
        email: value.email,
        department: value.department,
        jobTitle: value.jobTitle,
        seniorityLevel: value.seniorityLevel,
        country: value.country,
        currency,
        hireDate: this.toIsoDate(value.hireDate),
        startingSalary: value.startingSalary,
      })
      .subscribe((created) => this.router.navigate(['/employees', created.id]));
  }

  cancel(): void {
    const id = this.employeeId();
    this.router.navigate(id !== null ? ['/employees', id] : ['/employees']);
  }

  private toIsoDate(date: Date): string {
    return date.toISOString().slice(0, 10);
  }
}
