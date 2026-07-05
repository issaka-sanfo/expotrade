import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, NgIf],
  template: `
    <div style="display: flex; justify-content: center; margin-top: 100px;">
      <mat-card style="width: 400px; padding: 24px;">
        <mat-card-header><mat-card-title>Create an Account</mat-card-title></mat-card-header>
        <mat-card-content>
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
  error = '';
  success = '';

  constructor(private authService: AuthService) {}

  async register(): Promise<void> {
    this.error = '';
    this.success = '';
    try {
      await this.authService.register();
    } catch {
      this.error = 'Registration failed';
    }
  }
}
