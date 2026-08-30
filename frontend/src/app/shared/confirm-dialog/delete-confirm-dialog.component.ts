import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

export interface DeleteConfirmDialogData {
  title: string;
  message: string;
}

const CONFIRM_WORD = 'DELETE';

@Component({
  selector: 'app-delete-confirm-dialog',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon color="warn">warning</mat-icon>
      {{ data.title }}
    </h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
      <p>This action is <strong>permanent and cannot be undone</strong>.</p>
      <mat-form-field appearance="outline" class="confirm-field">
        <mat-label>Type {{ confirmWord }} to confirm</mat-label>
        <input matInput [(ngModel)]="typedValue" autocomplete="off" />
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancel</button>
      <button mat-raised-button color="warn" [disabled]="typedValue !== confirmWord" [mat-dialog-close]="true">
        Delete permanently
      </button>
    </mat-dialog-actions>
  `,
  styles: `
    .confirm-field {
      width: 100%;
      margin-top: 8px;
    }
    h2 {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  `,
})
export class DeleteConfirmDialogComponent {
  readonly data = inject<DeleteConfirmDialogData>(MAT_DIALOG_DATA);
  readonly confirmWord = CONFIRM_WORD;
  typedValue = '';
}
