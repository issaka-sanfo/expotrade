import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, NgIf],
  template: `
    <div style="display: flex; justify-content: center; margin-top: 100px;">
      <mat-card style="width: 400px; padding: 24px;">
        <mat-card-header><mat-card-title>Login to ExpoTrade</mat-card-title></mat-card-header>
        <mat-card-content>
          <p *ngIf="error" style="color: red">{{ error }}</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary" (click)="login()" style="width: 100%">Login</button>
          <button mat-stroked-button color="accent" routerLink="/register" style="width: 100%; margin-top: 12px;">Don't have an account? Register</button>
        </mat-card-actions>
      </mat-card>
    </div>
  `
})
export class LoginComponent {
  error = '';

  constructor(private authService: AuthService) {}

  async login(): Promise<void> {
    try {
      await this.authService.login();
    } catch {
      this.error = 'Login failed';
    }
  }
}
