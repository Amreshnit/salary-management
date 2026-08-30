export type EmployeeStatus = 'ACTIVE' | 'INACTIVE';

export interface Employee {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  seniorityLevel: string;
  country: string;
  state: string | null;
  address: string | null;
  currency: string;
  hireDate: string;
  status: EmployeeStatus;
  currentSalaryAmount: number | null;
  currentSalaryCurrency: string | null;
}

export interface EmployeeCreateRequest {
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  seniorityLevel: string;
  country: string;
  state: string | null;
  address: string | null;
  currency: string;
  hireDate: string;
  startingSalary: number;
}

export interface EmployeeUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  seniorityLevel: string;
  country: string;
  state: string | null;
  address: string | null;
  currency: string;
}

export interface EmployeeSearchParams {
  department?: string;
  country?: string;
  status?: EmployeeStatus;
  q?: string;
  page?: number;
  size?: number;
}
