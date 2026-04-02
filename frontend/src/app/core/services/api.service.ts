import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, Portfolio, Strategy, Trade, MarketData } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Portfolio
  getPortfolio(): Observable<Portfolio> {
    return this.http.get<Portfolio>(`${this.baseUrl}/portfolio`);
  }

  // Orders
  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/orders`);
  }

  placeOrder(order: Partial<Order>): Observable<Order> {
    return this.http.post<Order>(`${this.baseUrl}/orders`, order);
  }

  cancelOrder(orderId: string): Observable<Order> {
    return this.http.delete<Order>(`${this.baseUrl}/orders/${orderId}`);
  }

  // Trades
  getTrades(): Observable<Trade[]> {
    return this.http.get<Trade[]>(`${this.baseUrl}/trades`);
  }

  // Strategies
  getStrategies(): Observable<Strategy[]> {
    return this.http.get<Strategy[]>(`${this.baseUrl}/strategies`);
  }

  createStrategy(strategy: Partial<Strategy>): Observable<Strategy> {
    return this.http.post<Strategy>(`${this.baseUrl}/strategies`, strategy);
  }

  enableStrategy(id: string): Observable<Strategy> {
    return this.http.post<Strategy>(`${this.baseUrl}/strategies/${id}/enable`, {});
  }

  disableStrategy(id: string): Observable<Strategy> {
    return this.http.post<Strategy>(`${this.baseUrl}/strategies/${id}/disable`, {});
  }

  // Market Data
  getMarketData(symbol: string): Observable<MarketData> {
    return this.http.get<MarketData>(`${this.baseUrl}/market-data/${symbol}`);
  }
}
