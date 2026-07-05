import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../../environments/environment';

interface TokenResponse {
  access_token: string;
  refresh_token?: string;
  id_token?: string;
  expires_in: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenSubject = new BehaviorSubject<string | null>(localStorage.getItem('accessToken'));
  token$ = this.tokenSubject.asObservable();
  private readonly issuer = `${environment.keycloak.url.replace(/\/$/, '')}/realms/${environment.keycloak.realm}`;
  private readonly redirectUri = `${window.location.origin}/auth/callback`;

  async login(): Promise<void> {
    await this.startLogin();
  }

  async handleCallback(): Promise<void> {
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const state = params.get('state');
    const storedState = sessionStorage.getItem('pkce_state');
    const verifier = sessionStorage.getItem('pkce_verifier');

    if (!code || !state || !storedState || !verifier || state !== storedState) {
      throw new Error('Invalid Keycloak login callback');
    }

    const body = new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: environment.keycloak.clientId,
      code,
      redirect_uri: this.redirectUri,
      code_verifier: verifier
    });

    const response = await fetch(`${this.issuer}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body
    });

    if (!response.ok) {
      throw new Error('Keycloak token exchange failed');
    }

    this.storeTokens(await response.json());
    sessionStorage.removeItem('pkce_state');
    sessionStorage.removeItem('pkce_verifier');
  }

  logout(): void {
    const idToken = localStorage.getItem('idToken');
    this.clearTokens();
    const params = new URLSearchParams({
      client_id: environment.keycloak.clientId,
      post_logout_redirect_uri: window.location.origin
    });
    if (idToken) params.set('id_token_hint', idToken);
    window.location.href = `${this.issuer}/protocol/openid-connect/logout?${params}`;
  }

  async isAuthenticated(): Promise<boolean> {
    return !!(await this.getToken());
  }

  async getToken(): Promise<string | null> {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) return null;

    const expiresAt = Number(localStorage.getItem('accessTokenExpiresAt') || '0');
    if (Date.now() < expiresAt - 30000) {
      return accessToken;
    }

    return this.refreshToken();
  }

  private async startLogin(): Promise<void> {
    const verifier = this.randomString(96);
    const state = this.randomString(32);
    const challenge = await this.pkceChallenge(verifier);
    sessionStorage.setItem('pkce_verifier', verifier);
    sessionStorage.setItem('pkce_state', state);

    const params = new URLSearchParams({
      response_type: 'code',
      client_id: environment.keycloak.clientId,
      redirect_uri: this.redirectUri,
      scope: 'openid profile email',
      state,
      code_challenge: challenge,
      code_challenge_method: 'S256'
    });
    window.location.href = `${this.issuer}/protocol/openid-connect/auth?${params}`;
  }

  private async refreshToken(): Promise<string | null> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      this.clearTokens();
      return null;
    }

    const body = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: environment.keycloak.clientId,
      refresh_token: refreshToken
    });

    const response = await fetch(`${this.issuer}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body
    });

    if (!response.ok) {
      this.clearTokens();
      return null;
    }

    const tokens: TokenResponse = await response.json();
    this.storeTokens(tokens);
    return tokens.access_token;
  }

  private storeTokens(tokens: TokenResponse): void {
    localStorage.setItem('accessToken', tokens.access_token);
    if (tokens.refresh_token) localStorage.setItem('refreshToken', tokens.refresh_token);
    if (tokens.id_token) localStorage.setItem('idToken', tokens.id_token);
    localStorage.setItem('accessTokenExpiresAt', String(Date.now() + tokens.expires_in * 1000));
    this.tokenSubject.next(tokens.access_token);
  }

  private clearTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('idToken');
    localStorage.removeItem('accessTokenExpiresAt');
    this.tokenSubject.next(null);
  }

  private randomString(length: number): string {
    const bytes = new Uint8Array(length);
    crypto.getRandomValues(bytes);
    return this.base64Url(bytes);
  }

  private async pkceChallenge(verifier: string): Promise<string> {
    const data = new TextEncoder().encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return this.base64Url(new Uint8Array(digest));
  }

  private base64Url(bytes: Uint8Array): string {
    let binary = '';
    bytes.forEach(byte => binary += String.fromCharCode(byte));
    return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  }
}
