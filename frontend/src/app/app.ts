import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { filter, map } from 'rxjs';

interface NavItem {
  label: string;
  path: string;
  icon: string;
}

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly breakpointObserver = inject(BreakpointObserver);
  private readonly router = inject(Router);

  readonly navItems: NavItem[] = [
    { label: 'Employees', path: '/employees', icon: 'groups' },
    { label: 'Analytics', path: '/analytics', icon: 'insights' },
  ];

  readonly isHandset = toSignal(
    this.breakpointObserver.observe(Breakpoints.Handset).pipe(map((result) => result.matches)),
    { initialValue: false },
  );

  readonly sidenavOpened = signal(true);
  readonly sidenavMode = computed<'over' | 'side'>(() => (this.isHandset() ? 'over' : 'side'));

  readonly pageTitle = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map((event) => this.titleForUrl((event as NavigationEnd).urlAfterRedirects)),
    ),
    { initialValue: 'Employees' },
  );

  constructor() {
    this.sidenavOpened.set(!this.isHandset());
  }

  toggleSidenav(): void {
    this.sidenavOpened.set(!this.sidenavOpened());
  }

  closeSidenavOnMobile(): void {
    if (this.isHandset()) {
      this.sidenavOpened.set(false);
    }
  }

  private titleForUrl(url: string): string {
    if (url.startsWith('/analytics')) {
      return 'Analytics';
    }
    if (url.startsWith('/employees/new')) {
      return 'Add Employee';
    }
    if (url.match(/\/employees\/\d+\/edit/)) {
      return 'Edit Employee';
    }
    if (url.match(/\/employees\/\d+/)) {
      return 'Employee Profile';
    }
    return 'Employees';
  }
}
