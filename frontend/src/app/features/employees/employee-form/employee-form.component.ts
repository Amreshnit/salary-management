import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { map, startWith } from 'rxjs';

import { EmployeeService } from '../../../core/services/employee.service';
import { NotificationService } from '../../../core/services/notification.service';
import { SENIORITY_LEVELS } from '../../../core/models/reference-data';
import { CountryOption, StateOption, getAllCountries, getStatesOfCountry } from '../../../core/models/location-data';

@Component({
  selector: 'app-employee-form',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
  ],
  templateUrl: './employee-form.component.html',
  styleUrl: './employee-form.component.scss',
})
export class EmployeeFormComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly employeeService = inject(EmployeeService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly notifications = inject(NotificationService);

  readonly departments = signal<string[]>([]);
  readonly seniorityLevels = SENIORITY_LEVELS;
  readonly countries: CountryOption[] = getAllCountries();

  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly employeeId = signal<number | null>(null);
  readonly isEditMode = computed(() => this.employeeId() !== null);
  readonly statesForSelectedCountry = signal<StateOption[]>([]);

  readonly countrySearchControl = new FormControl<string | CountryOption>('', { nonNullable: true });
  readonly filteredCountries = toSignal(
    this.countrySearchControl.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterCountries(typeof value === 'string' ? value : value.name)),
    ),
    { initialValue: this.countries },
  );

  readonly form = this.formBuilder.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    department: ['', Validators.required],
    jobTitle: ['', Validators.required],
    seniorityLevel: ['', Validators.required],
    countryIsoCode: ['', Validators.required],
    stateIsoCode: [''],
    address: [''],
    hireDate: [new Date(), Validators.required],
    startingSalary: [0, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.employeeService.getDistinctDepartments().subscribe((departments) => this.departments.set(departments));

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.employeeId.set(id);
      this.form.controls.hireDate.clearValidators();
      this.form.controls.hireDate.updateValueAndValidity();
      this.form.controls.startingSalary.clearValidators();
      this.form.controls.startingSalary.updateValueAndValidity();
      this.loading.set(true);
      this.employeeService.getEmployeeById(id).subscribe((employee) => {
        const country = this.countries.find((candidate) => candidate.name === employee.country);
        if (country) {
          this.statesForSelectedCountry.set(getStatesOfCountry(country.isoCode));
        }
        const state = this.statesForSelectedCountry().find((candidate) => candidate.name === employee.state);

        this.form.patchValue({
          firstName: employee.firstName,
          lastName: employee.lastName,
          email: employee.email,
          department: employee.department,
          jobTitle: employee.jobTitle,
          seniorityLevel: employee.seniorityLevel,
          countryIsoCode: country?.isoCode ?? '',
          stateIsoCode: state?.isoCode ?? '',
          address: employee.address ?? '',
        });
        this.countrySearchControl.setValue(country ?? '');
        this.loading.set(false);
      });
    }
  }

  displayCountryName = (country: string | CountryOption): string => (typeof country === 'string' ? country : country.name);

  onCountrySelected(event: MatAutocompleteSelectedEvent): void {
    const country: CountryOption = event.option.value;
    this.form.controls.countryIsoCode.setValue(country.isoCode);
    this.onCountryChanged();
  }

  onCountryChanged(): void {
    const isoCode = this.form.controls.countryIsoCode.value;
    this.statesForSelectedCountry.set(isoCode ? getStatesOfCountry(isoCode) : []);
    this.form.controls.stateIsoCode.setValue('');
  }

  currencyForSelectedCountry(): string {
    const isoCode = this.form.controls.countryIsoCode.value;
    return this.countries.find((country) => country.isoCode === isoCode)?.currency ?? '';
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving.set(true);
    const value = this.form.getRawValue();
    const country = this.countries.find((candidate) => candidate.isoCode === value.countryIsoCode);
    const state = this.statesForSelectedCountry().find((candidate) => candidate.isoCode === value.stateIsoCode);
    const currency = country?.currency ?? '';
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
          country: country?.name ?? '',
          state: state?.name ?? null,
          address: value.address || null,
          currency,
        })
        .subscribe({
          next: () => {
            this.notifications.success('Employee updated successfully.');
            this.router.navigate(['/employees', id]);
          },
          error: () => this.saving.set(false),
        });
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
        country: country?.name ?? '',
        state: state?.name ?? null,
        address: value.address || null,
        currency,
        hireDate: this.toIsoDate(value.hireDate),
        startingSalary: value.startingSalary,
      })
      .subscribe({
        next: (created) => {
          this.notifications.success('Employee created successfully.');
          this.router.navigate(['/employees', created.id]);
        },
        error: () => this.saving.set(false),
      });
  }

  cancel(): void {
    const id = this.employeeId();
    this.router.navigate(id !== null ? ['/employees', id] : ['/employees']);
  }

  private filterCountries(searchText: string): CountryOption[] {
    const lower = searchText.trim().toLowerCase();
    if (!lower) {
      return this.countries;
    }
    return this.countries.filter((country) => country.name.toLowerCase().includes(lower));
  }

  private toIsoDate(date: Date): string {
    return date.toISOString().slice(0, 10);
  }
}
