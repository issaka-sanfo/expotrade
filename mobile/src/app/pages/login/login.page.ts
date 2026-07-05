import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent,
  IonButton, IonText, IonSpinner
} from '@ionic/angular/standalone';
import { AuthService } from '../../core/services/auth.service';
import { WebSocketService } from '../../core/services/websocket.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent,
    IonButton, IonText, IonSpinner
  ],
  template: `
    <ion-content class="ion-padding">
      <ion-card>
        <ion-card-header>
          <ion-card-title>ExpoTrade</ion-card-title>
        </ion-card-header>
        <ion-card-content>
          @if (error) {
            <ion-text color="danger"><p>{{ error }}</p></ion-text>
          }
          <ion-button expand="block" (click)="login()" [disabled]="loading">
            @if (loading) { <ion-spinner name="crescent"></ion-spinner> }
            @else { Login }
          </ion-button>
        </ion-card-content>
      </ion-card>
    </ion-content>
  `
})
export class LoginPage {
  error = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private wsService: WebSocketService,
    private router: Router
  ) {}

  async login(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      await this.authService.login();
    } catch {
      this.error = 'Login failed';
      this.loading = false;
    }
  }
}
