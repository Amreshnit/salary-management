export type SalaryChangeReason = 'HIRE' | 'RAISE' | 'PROMOTION' | 'ADJUSTMENT';

export interface SalaryRecord {
  id: number;
  amount: number;
  currency: string;
  effectiveFrom: string;
  effectiveTo: string | null;
  reason: SalaryChangeReason;
}

export interface SalaryRecordRequest {
  amount: number;
  currency: string;
  effectiveFrom: string;
  reason: SalaryChangeReason;
}
