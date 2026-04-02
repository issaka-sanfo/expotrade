import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AppState } from '../../store/app.state';
import { loadPortfolio } from '../../store/portfolio.reducer';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatProgressSpinnerModule],
  template: `
    <h2>Dashboard</h2>
    <ng-container *ngIf="(portfolio$ | async) as state">
      <mat-spinner *ngIf="state.loading" diameter="40"></mat-spinner>

      <div class="card-grid" *ngIf="state.portfolio as p">
        <mat-card>
          <mat-card-header><mat-card-title>Portfolio Value</mat-card-title></mat-card-header>
          <mat-card-content><h1>{{ p.totalValue | currency }}</h1></mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-header><mat-card-title>Cash Balance</mat-card-title></mat-card-header>
          <mat-card-content><h1>{{ p.cashBalance | currency }}</h1></mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-header><mat-card-title>Unrealized P&L</mat-card-title></mat-card-header>
          <mat-card-content>
            <h1 [class]="p.unrealizedPnl >= 0 ? 'positive' : 'negative'">
              {{ p.unrealizedPnl | currency }}
            </h1>
          </mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-header><mat-card-title>Day P&L</mat-card-title></mat-card-header>
          <mat-card-content>
            <h1 [class]="p.dayPnl >= 0 ? 'positive' : 'negative'">
              {{ p.dayPnl | currency }}
            </h1>
          </mat-card-content>
        </mat-card>
      </div>

      <mat-card *ngIf="state.portfolio as p" style="margin-top: 16px">
        <mat-card-header><mat-card-title>Positions</mat-card-title></mat-card-header>
        <mat-card-content>
          <table mat-table [dataSource]="p.positions" style="width: 100%">
            <ng-container matColumnDef="symbol">
              <th mat-header-cell *matHeaderCellDef>Symbol</th>
              <td mat-cell *matCellDef="let pos">{{ pos.symbol }}</td>
            </ng-container>
            <ng-container matColumnDef="quantity">
              <th mat-header-cell *matHeaderCellDef>Qty</th>
              <td mat-cell *matCellDef="let pos">{{ pos.quantity }}</td>
            </ng-container>
            <ng-container matColumnDef="avgPrice">
              <th mat-header-cell *matHeaderCellDef>Avg Price</th>
              <td mat-cell *matCellDef="let pos">{{ pos.averageEntryPrice | currency }}</td>
            </ng-container>
            <ng-container matColumnDef="currentPrice">
              <th mat-header-cell *matHeaderCellDef>Current</th>
              <td mat-cell *matCellDef="let pos">{{ pos.currentPrice | currency }}</td>
            </ng-container>
            <ng-container matColumnDef="pnl">
              <th mat-header-cell *matHeaderCellDef>P&L</th>
              <td mat-cell *matCellDef="let pos" [class]="pos.unrealizedPnl >= 0 ? 'positive' : 'negative'">
                {{ pos.unrealizedPnl | currency }}
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="positionColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: positionColumns;"></tr>
          </table>
        </mat-card-content>
      </mat-card>
    </ng-container>
  `
})
export class DashboardComponent implements OnInit {
  portfolio$ = this.store.select('portfolio');
  positionColumns = ['symbol', 'quantity', 'avgPrice', 'currentPrice', 'pnl'];

  constructor(private store: Store<AppState>) {}

  ngOnInit(): void {
    this.store.dispatch(loadPortfolio());
  }
}
