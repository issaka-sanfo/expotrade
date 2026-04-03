import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, NgIf],
  template: `
    <div style="display: flex; justify-content: center; margin-top: 100px;">
      <mat-card style="width: 400px; padding: 24px;">
        <mat-card-header><mat-card-title>Create an Account</mat-card-title></mat-card-header>
        <mat-card-content>
          <mat-form-field style="width: 100%">
            <mat-label>Username</mat-label>
            <input matInput [(ngModel)]="username">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Email</mat-label>
            <input matInput type="email" [(ngModel)]="email">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Password</mat-label>
            <input matInput type="password" [(ngModel)]="password">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Confirm Password</mat-label>
            <input matInput type="password" [(ngModel)]="confirmPassword">
          </mat-form-field>
          <p *ngIf="error" style="color: red">{{ error }}</p>
          <p *ngIf="success" style="color: green">{{ success }}</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary" (click)="register()" style="width: 100%">Register</button>
          <div style="text-align: center; margin-top: 12px;">
            <a mat-button routerLink="/login">Already have an account? Login</a>
          </div>
        </mat-card-actions>
      </mat-card>
    </div>
  `
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  confirmPassword = '';
  error = '';
  success = '';

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    this.error = '';
    this.success = '';

    if (this.password !== this.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    this.authService.register(this.username, this.email, this.password).subscribe({
      next: () => {
        this.success = 'Registration successful! Redirecting to login...';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => this.error = err.error?.error || err.error?.message || 'Registration failed'
    });
  }
}
