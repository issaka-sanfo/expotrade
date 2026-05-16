import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem,
  IonLabel, IonBadge, IonRefresher, IonRefresherContent, IonChip
} from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { Trade } from '../../shared/models/models';

@Component({
  selector: 'app-trades',
  standalone: true,
  imports: [
    CommonModule,
    IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem,
    IonLabel, IonBadge, IonRefresher, IonRefresherContent, IonChip
  ],
  template: `
    <ion-header>
      <ion-toolbar>
        <ion-title>Trades</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <ion-refresher slot="fixed" (ionRefresh)="refresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>
      <ion-list>
        @for (trade of trades; track trade.id) {
          <ion-item>
            <ion-label>
              <h2>{{ trade.symbol }}
                <ion-chip [color]="trade.side === 'BUY' ? 'success' : 'danger'">
                  {{ trade.side }}
                </ion-chip>
              </h2>
              <p>{{ trade.quantity }} &#64; {{ trade.price | currency }}</p>
              <p>Commission: {{ trade.commission | currency }}</p>
            </ion-label>
            <ion-label slot="end" class="ion-text-end">
              <p>{{ trade.executedAt | date:'short' }}</p>
            </ion-label>
          </ion-item>
        }
        @if (trades.length === 0) {
          <ion-item>
            <ion-label class="ion-text-center">No trades yet</ion-label>
          </ion-item>
        }
      </ion-list>
    </ion-content>
  `
})
export class TradesPage implements OnInit, OnDestroy {
  trades: Trade[] = [];
  private sub?: Subscription;

  constructor(private apiService: ApiService, private wsService: WebSocketService) {}

  ngOnInit(): void {
    this.loadTrades();
    this.sub = this.wsService.trades$.subscribe(envelope => {
      this.trades.unshift(envelope.data);
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadTrades(): void {
    this.apiService.getTrades().subscribe(trades => this.trades = trades);
  }

  refresh(event: any): void {
    this.loadTrades();
    setTimeout(() => event.target.complete(), 1000);
  }
}
