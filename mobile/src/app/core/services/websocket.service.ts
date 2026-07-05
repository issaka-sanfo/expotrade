import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';
import { WsEnvelope, Order, Trade, MarketData, StrategyConfig } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private socket: WebSocket | null = null;
  private messages$ = new Subject<WsEnvelope>();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private connected = false;

  constructor(private authService: AuthService) {}

  async connect(): Promise<void> {
    const token = await this.authService.getToken();
    if (!token) return;

    const url = `${environment.wsUrl}?token=${encodeURIComponent(token)}`;
    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      this.connected = true;
      this.reconnectAttempts = 0;
      this.subscribeAll();
    };

    this.socket.onmessage = (event) => {
      try {
        const envelope: WsEnvelope = JSON.parse(event.data);
        this.messages$.next(envelope);
      } catch (e) {
        console.error('Failed to parse WebSocket message', e);
      }
    };

    this.socket.onclose = () => {
      this.connected = false;
      this.attemptReconnect();
    };

    this.socket.onerror = () => {
      this.socket?.close();
    };
  }

  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    this.reconnectAttempts = this.maxReconnectAttempts;
    this.socket?.close();
    this.socket = null;
    this.connected = false;
  }

  get orders$(): Observable<WsEnvelope<Order>> {
    return this.messages$.pipe(
      filter(msg => msg.topic === 'orders'),
      map(msg => msg as WsEnvelope<Order>)
    );
  }

  get trades$(): Observable<WsEnvelope<Trade>> {
    return this.messages$.pipe(
      filter(msg => msg.topic === 'trades'),
      map(msg => msg as WsEnvelope<Trade>)
    );
  }

  get marketData$(): Observable<WsEnvelope<MarketData>> {
    return this.messages$.pipe(
      filter(msg => msg.topic === 'market-data'),
      map(msg => msg as WsEnvelope<MarketData>)
    );
  }

  get strategies$(): Observable<WsEnvelope<StrategyConfig>> {
    return this.messages$.pipe(
      filter(msg => msg.topic === 'strategies'),
      map(msg => msg as WsEnvelope<StrategyConfig>)
    );
  }

  subscribeToSymbols(symbols: string[]): void {
    this.send({ action: 'subscribe', topics: ['market-data'], symbols });
  }

  private subscribeAll(): void {
    this.send({ action: 'subscribe', topics: ['orders', 'trades', 'market-data', 'strategies'] });
  }

  private send(message: object): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message));
    }
  }

  private attemptReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) return;
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
    this.reconnectAttempts++;
    this.reconnectTimer = setTimeout(() => this.connect(), delay);
  }
}
