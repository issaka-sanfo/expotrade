import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { IonContent, IonSpinner } from '@ionic/angular/standalone';
import { AuthService } from '../../core/services/auth.service';
import { WebSocketService } from '../../core/services/websocket.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [IonContent, IonSpinner],
  template: `
    <ion-content class="ion-padding">
      <ion-spinner name="crescent"></ion-spinner>
    </ion-content>
  `
})
export class AuthCallbackPage implements OnInit {
  constructor(
    private authService: AuthService,
    private wsService: WebSocketService,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      await this.authService.handleCallback();
      await this.wsService.connect();
      await this.router.navigate(['/tabs']);
    } catch {
      await this.router.navigate(['/login']);
    }
  }
}
