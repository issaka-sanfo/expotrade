import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, NgIf],
  template: `
    <div style="display: flex; justify-content: center; margin-top: 100px;">
      <mat-card style="width: 400px; padding: 24px;">
        <mat-card-header><mat-card-title>Login to ExpoTrade</mat-card-title></mat-card-header>
        <mat-card-content>
          <mat-form-field style="width: 100%">
            <mat-label>Username</mat-label>
            <input matInput [(ngModel)]="username">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Password</mat-label>
            <input matInput type="password" [(ngModel)]="password">
          </mat-form-field>
          <p *ngIf="error" style="color: red">{{ error }}</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary" (click)="login()" style="width: 100%">Login</button>
        </mat-card-actions>
      </mat-card>
    </div>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.authService.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => this.error = err.error?.error || 'Login failed'
    });
  }
}
