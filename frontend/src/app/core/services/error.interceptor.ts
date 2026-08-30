import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { NotificationService } from './notification.service';

interface ApiErrorBody {
  message?: string;
  fieldErrors?: { field: string; message: string }[];
}

export const errorInterceptor: HttpInterceptorFn = (request, next) => {
  const notifications = inject(NotificationService);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      notifications.error(toUserFriendlyMessage(error));
      return throwError(() => error);
    }),
  );
};

function toUserFriendlyMessage(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'Unable to reach the server. Check your connection and try again.';
  }

  const body = error.error as ApiErrorBody | null;
  const firstFieldError = body?.fieldErrors?.[0]?.message;
  if (firstFieldError) {
    return firstFieldError;
  }
  if (body?.message && body.message !== 'Validation failed') {
    return body.message;
  }
  if (error.status === 404) {
    return 'The requested record could not be found.';
  }
  if (error.status === 409) {
    return 'That record already exists or conflicts with an existing one.';
  }
  if (error.status >= 500) {
    return 'Something went wrong on our end. Please try again in a moment.';
  }
  return 'Something went wrong. Please check your input and try again.';
}
