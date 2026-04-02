import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../core/services/websocket.service';
import { ApiService } from '../../core/services/api.service';
import { MarketData, Trade } from '../../shared/models/models';

@Component({
  selector: 'app-monitoring',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule],
  template: `
    <h2>Logs & Monitoring</h2>
    <div class="card-grid">
      <mat-card>
        <mat-card-header><mat-card-title>Live Market Data</mat-card-title></mat-card-header>
        <mat-card-content>
          <table mat-table [dataSource]="marketDataList" style="width: 100%">
            <ng-container matColumnDef="symbol">
              <th mat-header-cell *matHeaderCellDef>Symbol</th>
              <td mat-cell *matCellDef="let d">{{ d.symbol }}</td>
            </ng-container>
            <ng-container matColumnDef="bid">
              <th mat-header-cell *matHeaderCellDef>Bid</th>
              <td mat-cell *matCellDef="let d">{{ d.bid | number:'1.2-2' }}</td>
            </ng-container>
            <ng-container matColumnDef="ask">
              <th mat-header-cell *matHeaderCellDef>Ask</th>
              <td mat-cell *matCellDef="let d">{{ d.ask | number:'1.2-2' }}</td>
            </ng-container>
            <ng-container matColumnDef="last">
              <th mat-header-cell *matHeaderCellDef>Last</th>
              <td mat-cell *matCellDef="let d">{{ d.last | number:'1.2-2' }}</td>
            </ng-container>
            <ng-container matColumnDef="volume">
              <th mat-header-cell *matHeaderCellDef>Volume</th>
              <td mat-cell *matCellDef="let d">{{ d.volume | number:'1.0-0' }}</td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="mdColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: mdColumns;"></tr>
          </table>
        </mat-card-content>
      </mat-card>

      <mat-card>
        <mat-card-header><mat-card-title>Trade History</mat-card-title></mat-card-header>
        <mat-card-content>
          <table mat-table [dataSource]="trades" style="width: 100%">
            <ng-container matColumnDef="symbol">
              <th mat-header-cell *matHeaderCellDef>Symbol</th>
              <td mat-cell *matCellDef="let t">{{ t.symbol }}</td>
            </ng-container>
            <ng-container matColumnDef="side">
              <th mat-header-cell *matHeaderCellDef>Side</th>
              <td mat-cell *matCellDef="let t">{{ t.side }}</td>
            </ng-container>
            <ng-container matColumnDef="quantity">
              <th mat-header-cell *matHeaderCellDef>Qty</th>
              <td mat-cell *matCellDef="let t">{{ t.quantity }}</td>
            </ng-container>
            <ng-container matColumnDef="price">
              <th mat-header-cell *matHeaderCellDef>Price</th>
              <td mat-cell *matCellDef="let t">{{ t.price | currency }}</td>
            </ng-container>
            <ng-container matColumnDef="executedAt">
              <th mat-header-cell *matHeaderCellDef>Time</th>
              <td mat-cell *matCellDef="let t">{{ t.executedAt | date:'short' }}</td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="tradeColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: tradeColumns;"></tr>
          </table>
        </mat-card-content>
      </mat-card>
    </div>
  `
})
export class MonitoringComponent implements OnInit, OnDestroy {
  marketDataMap = new Map<string, MarketData>();
  marketDataList: MarketData[] = [];
  trades: Trade[] = [];
  mdColumns = ['symbol', 'bid', 'ask', 'last', 'volume'];
  tradeColumns = ['symbol', 'side', 'quantity', 'price', 'executedAt'];
  private sub: Subscription | null = null;

  constructor(private wsService: WebSocketService, private api: ApiService) {}

  ngOnInit(): void {
    this.api.getTrades().subscribe(t => this.trades = t);

    this.wsService.connect();
    setTimeout(() => this.wsService.subscribe(['AAPL', 'MSFT', 'GOOGL', 'TSLA']), 1000);

    this.sub = this.wsService.getMarketData$().subscribe(data => {
      this.marketDataMap.set(data.symbol, data);
      this.marketDataList = Array.from(this.marketDataMap.values());
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.wsService.disconnect();
  }
}
