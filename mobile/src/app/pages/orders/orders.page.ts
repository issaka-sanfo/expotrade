import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem,
  IonLabel, IonBadge, IonRefresher, IonRefresherContent, IonChip
} from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { Order } from '../../shared/models/models';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    CommonModule,
    IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem,
    IonLabel, IonBadge, IonRefresher, IonRefresherContent, IonChip
  ],
  template: `
    <ion-header>
      <ion-toolbar>
        <ion-title>Orders</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <ion-refresher slot="fixed" (ionRefresh)="refresh($event)">
        <ion-refresher-content></ion-refresher-content>
      </ion-refresher>
      <ion-list>
        @for (order of orders; track order.id) {
          <ion-item>
            <ion-label>
              <h2>{{ order.symbol }}
                <ion-chip [color]="order.side === 'BUY' ? 'success' : 'danger'">
                  {{ order.side }}
                </ion-chip>
              </h2>
              <p>{{ order.type }} | Qty: {{ order.quantity }} &#64; {{ order.price | currency }}</p>
              <p>{{ order.createdAt | date:'short' }}</p>
            </ion-label>
            <ion-badge slot="end" [color]="getStatusColor(order.status)">
              {{ order.status }}
            </ion-badge>
          </ion-item>
        }
        @if (orders.length === 0) {
          <ion-item>
            <ion-label class="ion-text-center">No orders yet</ion-label>
          </ion-item>
        }
      </ion-list>
    </ion-content>
  `
})
export class OrdersPage implements OnInit, OnDestroy {
  orders: Order[] = [];
  private sub?: Subscription;

  constructor(private apiService: ApiService, private wsService: WebSocketService) {}

  ngOnInit(): void {
    this.loadOrders();
    this.sub = this.wsService.orders$.subscribe(envelope => {
      const order = envelope.data;
      const idx = this.orders.findIndex(o => o.id === order.id);
      if (idx >= 0) {
        this.orders[idx] = order;
      } else {
        this.orders.unshift(order);
      }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadOrders(): void {
    this.apiService.getOrders().subscribe(orders => this.orders = orders);
  }

  refresh(event: any): void {
    this.loadOrders();
    setTimeout(() => event.target.complete(), 1000);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'FILLED': return 'success';
      case 'CANCELLED':
      case 'REJECTED': return 'danger';
      case 'PENDING':
      case 'SUBMITTED': return 'warning';
      default: return 'medium';
    }
  }
}
