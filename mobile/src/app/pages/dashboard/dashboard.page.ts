import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {
  IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardHeader,
  IonCardTitle, IonCardContent, IonList, IonItem, IonLabel, IonGrid,
  IonRow, IonCol, IonButton, IonButtons, IonIcon, IonBadge, IonRefresher,
  IonRefresherContent
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { logOutOutline } from 'ionicons/icons';
import { Subscription } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { AuthService } from '../../core/services/auth.service';
import { Portfolio, Trade } from '../../shared/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardHeader,
    IonCardTitle, IonCardContent, IonList, IonItem, IonLabel, IonGrid,
    IonRow, IonCol, IonButton, IonButtons, IonIcon, IonBadge, IonRefresher,
    IonRefresherContent
  ],
  template: `
    <ion-header>
      <ion-toolbar>
        <ion-title>Dashboard</ion-title>
        <ion-buttons slot="end">
          <ion-button (click)="logout()">
            <ion-icon name="log-out-outline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding">
      <ion-refresher slot="fixed" (ionRefresh)="refresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>

      @if (portfolio) {
        <ion-grid>
          <ion-row>
            <ion-col size="6">
              <ion-card>
                <ion-card-content>
                  <p>Total Value</p>
                  <h2>{{ portfolio.totalValue | currency }}</h2>
                </ion-card-content>
              </ion-card>
            </ion-col>
            <ion-col size="6">
              <ion-card>
                <ion-card-content>
                  <p>Cash</p>
                  <h2>{{ portfolio.cashBalance | currency }}</h2>
                </ion-card-content>
              </ion-card>
            </ion-col>
          </ion-row>
          <ion-row>
            <ion-col size="6">
              <ion-card>
                <ion-card-content>
                  <p>Day P&L</p>
                  <h2 [class]="portfolio.dayPnl >= 0 ? 'positive' : 'negative'">
                    {{ portfolio.dayPnl | currency }}
                  </h2>
                </ion-card-content>
              </ion-card>
            </ion-col>
            <ion-col size="6">
              <ion-card>
                <ion-card-content>
                  <p>Unrealized P&L</p>
                  <h2 [class]="portfolio.unrealizedPnl >= 0 ? 'positive' : 'negative'">
                    {{ portfolio.unrealizedPnl | currency }}
                  </h2>
                </ion-card-content>
              </ion-card>
            </ion-col>
          </ion-row>
        </ion-grid>

        @if (portfolio.positions.length > 0) {
          <ion-card>
            <ion-card-header>
              <ion-card-title>Positions</ion-card-title>
            </ion-card-header>
            <ion-list>
              @for (pos of portfolio.positions; track pos.id) {
                <ion-item>
                  <ion-label>
                    <h3>{{ pos.symbol }}</h3>
                    <p>{{ pos.quantity }} &#64; {{ pos.averageEntryPrice | currency }}</p>
                  </ion-label>
                  <ion-badge slot="end" [color]="pos.unrealizedPnl >= 0 ? 'success' : 'danger'">
                    {{ pos.unrealizedPnl | currency }}
                  </ion-badge>
                </ion-item>
              }
            </ion-list>
          </ion-card>
        }
      }

      @if (recentTrades.length > 0) {
        <ion-card>
          <ion-card-header>
            <ion-card-title>Recent Trades</ion-card-title>
          </ion-card-header>
          <ion-list>
            @for (trade of recentTrades; track trade.id) {
              <ion-item>
                <ion-label>
                  <h3>{{ trade.symbol }} - {{ trade.side }}</h3>
                  <p>{{ trade.quantity }} &#64; {{ trade.price | currency }}</p>
                </ion-label>
              </ion-item>
            }
          </ion-list>
        </ion-card>
      }
    </ion-content>
  `
})
export class DashboardPage implements OnInit, OnDestroy {
  portfolio: Portfolio | null = null;
  recentTrades: Trade[] = [];
  private subs: Subscription[] = [];

  constructor(
    private apiService: ApiService,
    private wsService: WebSocketService,
    private authService: AuthService,
    private router: Router
  ) {
    addIcons({ logOutOutline });
  }

  ngOnInit(): void {
    this.loadData();
    this.subs.push(
      this.wsService.trades$.subscribe(envelope => {
        this.recentTrades.unshift(envelope.data);
        if (this.recentTrades.length > 5) this.recentTrades.pop();
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  loadData(): void {
    this.apiService.getPortfolio().subscribe(p => this.portfolio = p);
    this.apiService.getTrades().subscribe(t => this.recentTrades = t.slice(0, 5));
  }

  refresh(event: any): void {
    this.loadData();
    setTimeout(() => event.target.complete(), 1000);
  }

  logout(): void {
    this.wsService.disconnect();
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
