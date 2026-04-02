import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { AuthService } from './core/services/auth.service';
import { AsyncPipe, NgIf } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule,
    MatIconModule, MatSidenavModule, MatListModule, AsyncPipe, NgIf],
  template: `
    <mat-toolbar color="primary">
      <mat-icon>trending_up</mat-icon>
      <span style="margin-left: 8px">ExpoTrade</span>
      <span style="flex: 1"></span>
      <ng-container *ngIf="auth.isAuthenticated()">
        <button mat-button routerLink="/dashboard" routerLinkActive="active">Dashboard</button>
        <button mat-button routerLink="/trading" routerLinkActive="active">Trading</button>
        <button mat-button routerLink="/strategies" routerLinkActive="active">Strategies</button>
        <button mat-button routerLink="/monitoring" routerLinkActive="active">Monitoring</button>
        <button mat-button (click)="auth.logout()">Logout</button>
      </ng-container>
    </mat-toolbar>
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`.active { background: rgba(255,255,255,0.15); }`]
})
export class AppComponent {
  constructor(public auth: AuthService) {}
}
