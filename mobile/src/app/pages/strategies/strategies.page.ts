import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem,
  IonLabel, IonBadge, IonToggle, IonRefresher, IonRefresherContent, IonChip
} from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { StrategyConfig } from '../../shared/models/models';

@Component({
  selector: 'app-strategies',
  standalone: true,
  imports: [
    CommonModule,
    IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem,
    IonLabel, IonBadge, IonToggle, IonRefresher, IonRefresherContent, IonChip
  ],
  template: `
    <ion-header>
      <ion-toolbar>
        <ion-title>Strategies</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <ion-refresher slot="fixed" (ionRefresh)="refresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>
      <ion-list>
        @for (strategy of strategies; track strategy.id) {
          <ion-item>
            <ion-label>
              <h2>{{ strategy.name }}</h2>
              <p>{{ strategy.type }} | {{ strategy.symbols.join(', ') }}</p>
              <p>
                <ion-chip color="medium">{{ strategy.brokerType }}</ion-chip>
              </p>
            </ion-label>
            <ion-toggle slot="end"
                        [checked]="strategy.status === 'ACTIVE'"
                        (ionChange)="toggleStrategy(strategy, $event)">
            </ion-toggle>
            <ion-badge slot="end" [color]="getStatusColor(strategy.status)">
              {{ strategy.status }}
            </ion-badge>
          </ion-item>
        }
        @if (strategies.length === 0) {
          <ion-item>
            <ion-label class="ion-text-center">No strategies configured</ion-label>
          </ion-item>
        }
      </ion-list>
    </ion-content>
  `
})
export class StrategiesPage implements OnInit, OnDestroy {
  strategies: StrategyConfig[] = [];
  private sub?: Subscription;

  constructor(private apiService: ApiService, private wsService: WebSocketService) {}

  ngOnInit(): void {
    this.loadStrategies();
    this.sub = this.wsService.strategies$.subscribe(envelope => {
      const updated = envelope.data;
      const idx = this.strategies.findIndex(s => s.id === updated.id);
      if (idx >= 0) {
        this.strategies[idx] = updated;
      } else {
        this.strategies.push(updated);
      }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadStrategies(): void {
    this.apiService.getStrategies().subscribe(s => this.strategies = s);
  }

  toggleStrategy(strategy: StrategyConfig, event: any): void {
    const enable = event.detail.checked;
    if (enable && strategy.status !== 'ACTIVE') {
      this.apiService.enableStrategy(strategy.id).subscribe();
    } else if (!enable && strategy.status === 'ACTIVE') {
      this.apiService.disableStrategy(strategy.id).subscribe();
    }
  }

  refresh(event: any): void {
    this.loadStrategies();
    setTimeout(() => event.target.complete(), 1000);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'PAUSED': return 'warning';
      case 'ERROR': return 'danger';
      default: return 'medium';
    }
  }
}
