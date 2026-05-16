import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent,
  IonItem, IonLabel, IonInput, IonButton, IonText, IonSpinner
} from '@ionic/angular/standalone';
import { AuthService } from '../../core/services/auth.service';
import { WebSocketService } from '../../core/services/websocket.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent,
    IonItem, IonLabel, IonInput, IonButton, IonText, IonSpinner
  ],
  template: `
    <ion-content class="ion-padding">
      <ion-card>
        <ion-card-header>
          <ion-card-title>ExpoTrade</ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <ion-item>
            <ion-input label="Username" labelPlacement="floating"
                       [(ngModel)]="username" type="text"></ion-input>
          </ion-item>
          <ion-item>
            <ion-input label="Password" labelPlacement="floating"
                       [(ngModel)]="password" type="password"></ion-input>
          </ion-item>
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
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private wsService: WebSocketService,
    private router: Router
  ) {}

  login(): void {
    this.loading = true;
    this.error = '';
    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.wsService.connect();
        this.router.navigate(['/tabs']);
      },
      error: () => {
        this.error = 'Invalid username or password';
        this.loading = false;
      }
    });
  }
}
