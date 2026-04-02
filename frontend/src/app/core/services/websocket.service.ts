import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MarketData } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private socket: WebSocket | null = null;
  private marketDataSubject = new Subject<MarketData>();

  connect(): void {
    if (this.socket) return;

    this.socket = new WebSocket(`${environment.wsUrl}/market-data`);
    this.socket.onmessage = (event) => {
      const data: MarketData = JSON.parse(event.data);
      this.marketDataSubject.next(data);
    };
    this.socket.onclose = () => { this.socket = null; };
    this.socket.onerror = (err) => console.error('WebSocket error:', err);
  }

  subscribe(symbols: string[], broker: string = 'IBKR'): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify({ action: 'subscribe', symbols, broker }));
    }
  }

  getMarketData$(): Observable<MarketData> {
    return this.marketDataSubject.asObservable();
  }

  disconnect(): void {
    this.socket?.close();
    this.socket = null;
  }
}
