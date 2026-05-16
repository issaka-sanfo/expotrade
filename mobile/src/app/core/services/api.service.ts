import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, Trade, Portfolio, StrategyConfig, MarketData } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getPortfolio(): Observable<Portfolio> {
    return this.http.get<Portfolio>(`${this.baseUrl}/portfolio`);
  }

  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/orders`);
  }

  placeOrder(order: Partial<Order>): Observable<Order> {
    return this.http.post<Order>(`${this.baseUrl}/orders`, order);
  }

  cancelOrder(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/orders/${id}`);
  }

  getTrades(): Observable<Trade[]> {
    return this.http.get<Trade[]>(`${this.baseUrl}/trades`);
  }

  getStrategies(): Observable<StrategyConfig[]> {
    return this.http.get<StrategyConfig[]>(`${this.baseUrl}/strategies`);
  }

  enableStrategy(id: string): Observable<StrategyConfig> {
    return this.http.put<StrategyConfig>(`${this.baseUrl}/strategies/${id}/enable`, {});
  }

  disableStrategy(id: string): Observable<StrategyConfig> {
    return this.http.put<StrategyConfig>(`${this.baseUrl}/strategies/${id}/disable`, {});
  }

  getMarketData(symbol: string): Observable<MarketData> {
    return this.http.get<MarketData>(`${this.baseUrl}/market-data/${symbol}`);
  }
}
