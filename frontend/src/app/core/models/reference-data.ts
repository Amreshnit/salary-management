export const DEPARTMENTS = [
  'Engineering',
  'Product',
  'Sales',
  'Marketing',
  'Human Resources',
  'Finance',
  'Operations',
  'Customer Support',
  'Legal',
  'IT',
];

export const SENIORITY_LEVELS = ['Intern', 'Junior', 'Mid', 'Senior', 'Lead', 'Manager', 'Director', 'VP'];

export interface CountryCurrency {
  country: string;
  currency: string;
}

export const COUNTRIES: CountryCurrency[] = [
  { country: 'United States', currency: 'USD' },
  { country: 'United Kingdom', currency: 'GBP' },
  { country: 'Germany', currency: 'EUR' },
  { country: 'France', currency: 'EUR' },
  { country: 'Canada', currency: 'CAD' },
  { country: 'Australia', currency: 'AUD' },
  { country: 'Singapore', currency: 'SGD' },
  { country: 'India', currency: 'INR' },
  { country: 'Japan', currency: 'JPY' },
  { country: 'Brazil', currency: 'BRL' },
];
