import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardContent,
  IonList, IonItem, IonLabel, IonGrid, IonRow, IonCol, IonBadge
} from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../core/services/websocket.service';
import { MarketData } from '../../shared/models/models';

@Component({
  selector: 'app-market-data',
  standalone: true,
  imports: [
    CommonModule,
    IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardContent,
    IonList, IonItem, IonLabel, IonGrid, IonRow, IonCol, IonBadge
  ],
  template: `
    <ion-header>
      <ion-toolbar>
        <ion-title>Market Data</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding">
      @if (symbols.length === 0) {
        <ion-card>
          <ion-card-content>
            <p>Waiting for market data...</p>
          </ion-card-content>
        </ion-card>
      }
      @for (symbol of symbols; track symbol) {
        <ion-card>
          <ion-card-content>
            <ion-grid>
              <ion-row>
                <ion-col size="4">
                  <h2>{{ symbol }}</h2>
                </ion-col>
                <ion-col size="4" class="ion-text-center">
                  <p>Last</p>
                  <h3 [class]="getPriceDirection(symbol)">
                    {{ marketDataMap.get(symbol)?.last | currency }}
                  </h3>
                </ion-col>
                <ion-col size="4" class="ion-text-end">
                  <p>Volume</p>
                  <h3>{{ marketDataMap.get(symbol)?.volume | number }}</h3>
                </ion-col>
              </ion-row>
              <ion-row>
                <ion-col size="3">
                  <small>Bid</small><br/>
                  <span>{{ marketDataMap.get(symbol)?.bid | currency }}</span>
                </ion-col>
                <ion-col size="3">
                  <small>Ask</small><br/>
                  <span>{{ marketDataMap.get(symbol)?.ask | currency }}</span>
                </ion-col>
                <ion-col size="3">
                  <small>High</small><br/>
                  <span class="positive">{{ marketDataMap.get(symbol)?.high | currency }}</span>
                </ion-col>
                <ion-col size="3">
                  <small>Low</small><br/>
                  <span class="negative">{{ marketDataMap.get(symbol)?.low | currency }}</span>
                </ion-col>
              </ion-row>
            </ion-grid>
          </ion-card-content>
        </ion-card>
      }
    </ion-content>
  `
})
export class MarketDataPage implements OnInit, OnDestroy {
  marketDataMap = new Map<string, MarketData>();
  previousPrices = new Map<string, number>();
  symbols: string[] = [];
  private sub?: Subscription;

  constructor(private wsService: WebSocketService) {}

  ngOnInit(): void {
    this.wsService.subscribeToSymbols(['AAPL', 'MSFT', 'GOOGL', 'TSLA', 'AMZN']);

    this.sub = this.wsService.marketData$.subscribe(envelope => {
      const data = envelope.data;
      const prev = this.marketDataMap.get(data.symbol);
      if (prev) {
        this.previousPrices.set(data.symbol, prev.last);
      }
      this.marketDataMap.set(data.symbol, data);
      if (!this.symbols.includes(data.symbol)) {
        this.symbols = [...this.symbols, data.symbol];
      }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  getPriceDirection(symbol: string): string {
    const current = this.marketDataMap.get(symbol)?.last ?? 0;
    const prev = this.previousPrices.get(symbol) ?? current;
    if (current > prev) return 'positive';
    if (current < prev) return 'negative';
    return '';
  }
}
