import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `<div style="padding: 24px;">Completing sign in...</div>`
})
export class AuthCallbackComponent implements OnInit {
  constructor(private authService: AuthService, private router: Router) {}

  async ngOnInit(): Promise<void> {
    try {
      await this.authService.handleCallback();
      await this.router.navigate(['/dashboard']);
    } catch {
      await this.router.navigate(['/login']);
    }
  }
}
